package com.khoavdse170395.questionservice.service.impl;

import com.khoavdse170395.questionservice.model.*;
import com.khoavdse170395.questionservice.model.dto.request.MockAnswerRequestDTO;
import com.khoavdse170395.questionservice.model.dto.request.MockAttemptRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockAnswerResponseDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockAnswerSubmissionResultDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockAttemptResponseDTO;
import com.khoavdse170395.questionservice.repository.MockAnswerRepository;
import com.khoavdse170395.questionservice.repository.MockAttemptRepository;
import com.khoavdse170395.questionservice.repository.MockQuestionRepository;
import com.khoavdse170395.questionservice.repository.MockOptionRepository;
import com.khoavdse170395.questionservice.repository.MockTestRepository;
import com.khoavdse170395.questionservice.service.MockAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.khoavdse170395.questionservice.client.AccountServiceClient;
import com.khoavdse170395.questionservice.client.dto.AccountDTO;
import com.khoavdse170395.questionservice.client.AIGradingClient;
import com.khoavdse170395.questionservice.client.dto.ai.GradingApiResponse;
import com.khoavdse170395.questionservice.client.dto.ai.QuestionAnswerRequest;
import com.khoavdse170395.questionservice.client.payment.PaymentServiceClient;
import com.khoavdse170395.questionservice.client.payment.dto.MembershipDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MockAttemptServiceImpl implements MockAttemptService {

    private final MockAttemptRepository mockAttemptRepository;
    private final MockAnswerRepository mockAnswerRepository;
    private final MockQuestionRepository mockQuestionRepository;
    private final MockOptionRepository mockOptionRepository;
    private final MockTestRepository mockTestRepository;
    private final AccountServiceClient accountServiceClient;
    private final AIGradingClient aiGradingClient;
    private final PaymentServiceClient paymentServiceClient;

    @Override
    public MockAttemptResponseDTO create(MockAttemptRequestDTO dto) {
        MockAttempt entity = new MockAttempt();
        apply(dto, entity);
        MockAttempt saved;
        try {
            saved = mockAttemptRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            // In case of race condition with DB unique/partial unique index
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Attempt already in progress", ex);
        }
        return toResponse(saved);
    }

    @Override
    public MockAttemptResponseDTO update(Long id, MockAttemptRequestDTO dto) {
        MockAttempt entity = mockAttemptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MockAttempt not found: " + id));
        apply(dto, entity);
        MockAttempt saved = mockAttemptRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        mockAttemptRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MockAttemptResponseDTO getById(Long id) {
        return mockAttemptRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("MockAttempt not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MockAttemptResponseDTO> getAll() {
        return mockAttemptRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public MockAnswerSubmissionResultDTO addAnswer(Long attemptId, MockAnswerRequestDTO answerDTO) {
        MockAttempt attempt = mockAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("MockAttempt not found: " + attemptId));

        ensureOwnership(attempt);

        if (attempt.getStatus() == AttemptStatus.FINISHED) {
            return MockAnswerSubmissionResultDTO.builder()
                    .finalized(true)
                    .finalizedAttempt(toResponse(attempt))
                    .message("Attempt already finished.")
                    .build();
        }

        var now = LocalDateTime.now();
        if (attempt.getStartTime() != null && now.isBefore(attempt.getStartTime())) {
            throw new IllegalStateException("Attempt has not started yet");
        }
        if (attempt.getEndTime() != null && now.isAfter(attempt.getEndTime())) {
            MockAttempt finalized = finalizeAttemptInternal(attempt);
            return MockAnswerSubmissionResultDTO.builder()
                    .finalized(true)
                    .finalizedAttempt(toResponse(finalized))
                    .message("Attempt duration elapsed. Returning graded result.")
                    .build();
        }

        if (answerDTO.getMockQuestionId() == null) {
            throw new IllegalArgumentException("mockQuestionId is required");
        }

        MockQuestion question = mockQuestionRepository.findById(answerDTO.getMockQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("MockQuestion not found: " + answerDTO.getMockQuestionId()));

        // If an answer for this question in this attempt already exists, update it (idempotent)
        MockAnswer entity = mockAnswerRepository
                .findFirstByMockAttempt_IdAndMockQuestion_Id(attemptId, question.getId())
                .orElse(new MockAnswer());
        entity.setQuestionType(question.getQuestionType());
        entity.setAnswerText(answerDTO.getAnswerText());
        entity.setComments(null);
        entity.setAnswerPoint(null); // will be graded after end time
        entity.setMockQuestion(question);
        entity.setMockAttempt(attempt);

        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICES) {
            if (answerDTO.getMockOptionId() == null) {
                throw new IllegalArgumentException("mockOptionId is required for MULTIPLE_CHOICES");
            }
            MockOption option = mockOptionRepository.findById(answerDTO.getMockOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("MockOption not found: " + answerDTO.getMockOptionId()));
            entity.setMockOption(option);
        } else {
            // ESSAY: ignore option
            entity.setMockOption(null);
        }

        MockAnswer saved = mockAnswerRepository.save(entity);

        // Attach to attempt answers list (optional if using owning side)
//        if (attempt.getMockAnswers() != null) {
//            attempt.getMockAnswers().add(saved);
//        }

        return MockAnswerSubmissionResultDTO.builder()
                .answer(toAnswerResponse(saved))
                .finalized(false)
                .build();
    }

    @Override
    public MockAttemptResponseDTO finalizeAndGrade(Long attemptId) {
        MockAttempt attempt = mockAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("MockAttempt not found: " + attemptId));

        ensureOwnership(attempt);
        MockAttempt savedAttempt = finalizeAttemptInternal(attempt);
        return toResponse(savedAttempt);
    }

    @Override
    public int finalizeExpiredAttempts() {
        var now = LocalDateTime.now();
        List<MockAttempt> expired = mockAttemptRepository
                .findByStatusAndEndTimeBefore(AttemptStatus.IN_PROGRESS, now);
        int processed = 0;
        for (MockAttempt attempt : expired) {
            finalizeAttemptInternal(attempt);
            processed++;
        }
        return processed;
    }

    @Override
    public MockAttemptResponseDTO startAttempt(Long testId) {
        Long currentUserId = getCurrentUserId();
        MockTest test = mockTestRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("MockTest not found: " + testId));

        var now = LocalDateTime.now();
        var existing = mockAttemptRepository
                .findFirstByUserIdAndMockTest_IdAndStatus(currentUserId, testId, AttemptStatus.IN_PROGRESS);

        if (existing.isPresent()) {
            MockAttempt existingAttempt = existing.get();
            LocalDateTime endTime = existingAttempt.getEndTime();
            if (endTime == null || now.isBefore(endTime)) {
                return toResponse(existingAttempt);
            }
            finalizeAttemptInternal(existingAttempt);
        }

        MembershipTier requiredTier = test.getRequiredTier() != null ? test.getRequiredTier() : MembershipTier.BASIC;
        MembershipDTO membership = ensureEligibleMembership(currentUserId, requiredTier);

        var duration = test.getDuration();

        MockAttempt entity = MockAttempt.builder()
                .userId(currentUserId)
                .userSubscriptionId(membership != null ? membership.getId() : null)
                .attemptPoint(0)
                .duration(duration)
                .startTime(now)
                .endTime(duration != null ? now.plus(duration) : null)
                .mockTest(test)
                .status(AttemptStatus.IN_PROGRESS)
                .build();

        MockAttempt saved = mockAttemptRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MockAttemptResponseDTO getMyAttemptById(Long id) {
        MockAttempt attempt = mockAttemptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MockAttempt not found: " + id));
        ensureOwnership(attempt);
        return toResponse(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MockAttemptResponseDTO> getMyAttempts() {
        Long currentUserId = getCurrentUserId();
        return mockAttemptRepository.findByUserId(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    private void ensureOwnership(MockAttempt attempt) {
        Long currentUserId = getCurrentUserId();
        if (attempt.getUserId() == null || !attempt.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not own this attempt");
        }
    }

    private Long getCurrentUserId() {
        AccountDTO account = accountServiceClient.getMe();
        if (account == null || account.getUserId() == null) {
            throw new BadCredentialsException("User not found in account service");
        }
        return account.getUserId();
    }

    private MembershipDTO ensureEligibleMembership(Long userId, MembershipTier requiredTier) {
        try {
            List<MembershipDTO> memberships = paymentServiceClient.getMembershipsByUserId(userId);
            if (memberships == null || memberships.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Membership is required to start this test.");
            }

            MembershipDTO membership = memberships.stream()
                    .filter(Objects::nonNull)
                    .filter(m -> m.getStatus() == MembershipStatus.ACTIVE)
                    .max(Comparator.comparingInt(m -> m.getTier() != null ? m.getTier().getRank() : Integer.MIN_VALUE))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Membership is not active."));

            MembershipTier userTier = membership.getTier();
            if (userTier == null || !userTier.isAtLeast(requiredTier)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Membership tier does not meet test requirement.");
            }

            return membership;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to verify membership at this time.", ex);
        }
    }

    private MockAttempt finalizeAttemptInternal(MockAttempt attempt) {
        if (attempt.getStatus() == AttemptStatus.FINISHED) {
            return attempt;
        }

        List<MockAnswer> answers = attempt.getMockAnswers();
        if (answers == null || answers.isEmpty()) {
            attempt.setAttemptPoint(0);
            attempt.setStatus(AttemptStatus.FINISHED);
            return mockAttemptRepository.save(attempt);
        }

        int total = 0;
        for (MockAnswer answer : answers) {
            int pts = calculatePoints(answer);
            answer.setAnswerPoint(pts);
            total += pts;
        }

        mockAnswerRepository.saveAll(answers);
        attempt.setAttemptPoint(total);
        attempt.setStatus(AttemptStatus.FINISHED);
        return mockAttemptRepository.save(attempt);
    }

    private int calculatePoints(MockAnswer answer) {
        int pts = 0;
        if (answer.getQuestionType() == QuestionType.ESSAY) {
            MockQuestion q = answer.getMockQuestion();
            String questionText = q != null ? q.getQuestion() : null;
            String answerText = answer.getAnswerText();
            try {
                if (questionText != null && answerText != null && !answerText.isBlank()) {
                    GradingApiResponse res = aiGradingClient.gradeFRQForLiteratureSubject(
                            new QuestionAnswerRequest(questionText, answerText)
                    );
                    if (res != null && res.getData() != null) {
                        if (res.getData().getFeedback() != null) {
                            answer.setComments(res.getData().getFeedback());
                        }
                        if (res.getData().getTotal() != null) {
                            double total10 = res.getData().getTotal();
                            int qPoint = q != null && q.getPoint() != null ? q.getPoint() : 0;
                            pts = (int) Math.round((total10 / 10.0) * qPoint);
                        }
                    }
                }
            } catch (Exception ignored) {
                // keep pts = 0 on failure to grade automatically
            }
        } else if (answer.getQuestionType() == QuestionType.MULTIPLE_CHOICES) {
            MockQuestion q = answer.getMockQuestion();
            if (q != null && q.getAnswer() != null && answer.getMockOption() != null) {
                if (q.getAnswer().getId().equals(answer.getMockOption().getId())) {
                    pts = q.getPoint() != null ? q.getPoint() : 0;
                }
            }
        }
        return pts;
    }

    private void apply(MockAttemptRequestDTO dto, MockAttempt entity) {
        entity.setUserId(dto.getUserId());
        entity.setUserSubscriptionId(dto.getUserSubscriptionId());
        entity.setAttemptPoint(dto.getAttemptPoint());
        entity.setDuration(dto.getDuration());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setStatus(dto.getStatus());

        if (dto.getMockTestId() != null) {
            MockTest mt = mockTestRepository.findById(dto.getMockTestId())
                    .orElseThrow(() -> new IllegalArgumentException("MockTest not found: " + dto.getMockTestId()));
            entity.setMockTest(mt);
        }

        if (dto.getMockAnswerIds() != null) {
            List<Long> answerIds = dto.getMockAnswerIds().stream()
                    .filter(Objects::nonNull)
                    .toList();
            List<MockAnswer> answers = answerIds.isEmpty() ? new ArrayList<>() : mockAnswerRepository.findAllById(answerIds);
            entity.setMockAnswers(answers);
        }
    }

    private MockAttemptResponseDTO toResponse(MockAttempt entity) {
        List<MockAnswerResponseDTO> answerDTOs = Optional.ofNullable(entity.getMockAnswers())
                .map(list -> list.stream().map(this::toAnswerResponse).toList())
                .orElse(List.of());

        Integer maxPoint = entity.getMockTest() != null ? entity.getMockTest().getTotalPoint() : null;

        return MockAttemptResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .userSubscriptionId(entity.getUserSubscriptionId())
                .attemptPoint(entity.getAttemptPoint())
                .maxPoint(maxPoint)
                .duration(entity.getDuration())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .mockTestId(entity.getMockTest() != null ? entity.getMockTest().getId() : null)
                .status(entity.getStatus())
                .mockAnswers(answerDTOs)
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .build();
    }

    private MockAnswerResponseDTO toAnswerResponse(MockAnswer a) {
        return MockAnswerResponseDTO.builder()
                .id(a.getId())
                .accountId(a.getMockAttempt() != null ? a.getMockAttempt().getUserId() : null)
                .answerPoint(a.getAnswerPoint())
                .maxPoint(a.getMockQuestion().getPoint())
                .questionType(a.getQuestionType())
                .answerText(a.getAnswerText())
                .comments(a.getComments())
                .mockOptionId(a.getMockOption() != null ? a.getMockOption().getId() : null)
                .mockQuestionId(a.getMockQuestion() != null ? a.getMockQuestion().getId() : null)
                .mockAttemptId(a.getMockAttempt() != null ? a.getMockAttempt().getId() : null)
                .createdDate(a.getCreatedDate())
                .updatedDate(a.getUpdatedDate())
                .build();
    }
}
