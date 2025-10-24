package com.khoavdse170395.questionservice.service.impl;

import com.khoavdse170395.questionservice.model.MockAnswer;
import com.khoavdse170395.questionservice.model.MockAttempt;
import com.khoavdse170395.questionservice.model.dto.MockAnswerDTO;
import com.khoavdse170395.questionservice.model.dto.MockAttemptDTO;
import com.khoavdse170395.questionservice.repository.MockAnswerRepository;
import com.khoavdse170395.questionservice.repository.MockAttemptRepository;
import com.khoavdse170395.questionservice.service.MockAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MockAttemptServiceImpl implements MockAttemptService {

    private final MockAttemptRepository mockAttemptRepository;
    private final MockAnswerRepository mockAnswerRepository;

    @Override
    public MockAttemptDTO create(MockAttemptDTO dto) {
        MockAttempt entity = new MockAttempt();
        apply(dto, entity);
        MockAttempt saved = mockAttemptRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public MockAttemptDTO update(Long id, MockAttemptDTO dto) {
        MockAttempt entity = mockAttemptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MockAttempt not found: " + id));
        apply(dto, entity);
        MockAttempt saved = mockAttemptRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        mockAttemptRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MockAttemptDTO getById(Long id) {
        return mockAttemptRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("MockAttempt not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MockAttemptDTO> getAll() {
        return mockAttemptRepository.findAll().stream().map(this::toDTO).toList();
    }

    private void apply(MockAttemptDTO dto, MockAttempt entity) {
        entity.setUserSubscriptionId(dto.getUserSubscriptionId());
        entity.setAttemptPoint(dto.getAttemptPoint());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());

        if (dto.getMockAnswers() != null) {
            List<Long> answerIds = dto.getMockAnswers().stream()
                    .map(MockAnswerDTO::getId)
                    .filter(Objects::nonNull)
                    .toList();
            List<MockAnswer> answers = answerIds.isEmpty() ? new ArrayList<>() : mockAnswerRepository.findAllById(answerIds);
            entity.setMockAnswers(answers);
        }
    }

    private MockAttemptDTO toDTO(MockAttempt entity) {
        List<MockAnswerDTO> answerDTOs = Optional.ofNullable(entity.getMockAnswers())
                .map(list -> list.stream().map(this::toAnswerDTO).toList())
                .orElse(List.of());

        return MockAttemptDTO.builder()
                .id(entity.getId())
                .userSubscriptionId(entity.getUserSubscriptionId())
                .attemptPoint(entity.getAttemptPoint())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .mockAnswers(answerDTOs)
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .build();
    }

    private MockAnswerDTO toAnswerDTO(MockAnswer a) {
        return MockAnswerDTO.builder()
                .id(a.getId())
                .accountId(a.getAccountId())
                .answerPoint(a.getAnswerPoint())
                .questionType(a.getQuestionType())
                .mockOptionId(a.getMockOption() != null ? a.getMockOption().getId() : null)
                .mockQuestionId(a.getMockQuestion() != null ? a.getMockQuestion().getId() : null)
                .mockAttemptId(a.getMockAttempt() != null ? a.getMockAttempt().getId() : null)
                .createdDate(a.getCreatedDate())
                .updatedDate(a.getUpdatedDate())
                .build();
    }
}
