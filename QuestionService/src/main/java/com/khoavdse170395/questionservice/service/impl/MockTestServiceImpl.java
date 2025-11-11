package com.khoavdse170395.questionservice.service.impl;

import com.khoavdse170395.questionservice.client.lesson.LessonServiceClient;
import com.khoavdse170395.questionservice.client.lesson.dto.LessonApiResponse;
import com.khoavdse170395.questionservice.client.lesson.dto.LessonDTO;
import com.khoavdse170395.questionservice.model.MockOption;
import com.khoavdse170395.questionservice.model.MockQuestion;
import com.khoavdse170395.questionservice.model.MockTest;
import com.khoavdse170395.questionservice.model.MembershipTier;
import com.khoavdse170395.questionservice.model.dto.request.MockTestRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockOptionResponseDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockQuestionResponseDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockTestResponseDTO;
import com.khoavdse170395.questionservice.repository.MockQuestionRepository;
import com.khoavdse170395.questionservice.repository.MockTestRepository;
import com.khoavdse170395.questionservice.service.MockTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MockTestServiceImpl implements MockTestService {

    private final MockTestRepository mockTestRepository;
    private final MockQuestionRepository mockQuestionRepository;
    private final LessonServiceClient lessonServiceClient;

    @Override
    public MockTestResponseDTO create(MockTestRequestDTO dto) {
        MockTest entity = new MockTest();
        apply(dto, entity);
        MockTest saved = mockTestRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public MockTestResponseDTO update(Long id, MockTestRequestDTO dto) {
        MockTest entity = mockTestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MockTest not found: " + id));
        apply(dto, entity);
        MockTest saved = mockTestRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        mockTestRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MockTestResponseDTO getById(Long id) {
        return mockTestRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("MockTest not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MockTestResponseDTO> getAll() {
        return mockTestRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MockTestResponseDTO> getByLessonId(String lessonId) {
        LessonApiResponse<LessonDTO> response = lessonServiceClient.getLessonById(lessonId);
        LessonDTO lesson = response != null ? response.getData() : null;
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson not found: " + lessonId);
        }
        return mockTestRepository.findByLessonId(lesson.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private void apply(MockTestRequestDTO dto, MockTest entity) {
        entity.setName(dto.getName());
        entity.setDuration(dto.getDurationSeconds() != null ? Duration.ofSeconds(dto.getDurationSeconds()) : null);
        entity.setTotalPoint(dto.getTotalPoint());
        entity.setLessonId(dto.getLessonId());
        entity.setRequiredTier(dto.getRequiredTier() != null ? dto.getRequiredTier() : MembershipTier.BASIC);

        if (dto.getQuestionIds() != null) {
            List<Long> ids = dto.getQuestionIds().stream()
                    .filter(Objects::nonNull)
                    .toList();
            List<MockQuestion> questions = ids.isEmpty() ? new ArrayList<>() : mockQuestionRepository.findAllById(ids);
            entity.setQuestions(questions);
            for (MockQuestion q : questions) {
                q.setTest(entity);
            }
        }
    }

    private MockTestResponseDTO toResponse(MockTest entity) {
        List<MockQuestionResponseDTO> questionDTOs = Optional.ofNullable(entity.getQuestions())
                .map(list -> list.stream().map(this::toQuestionResponse).toList())
                .orElse(List.of());

        return MockTestResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .durationSeconds(entity.getDuration() != null ? entity.getDuration().toSeconds() : null)
                .totalPoint(entity.getTotalPoint())
                .lessonId(entity.getLessonId())
                .requiredTier(entity.getRequiredTier())
                .questions(questionDTOs)
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .build();
    }

    private MockQuestionResponseDTO toQuestionResponse(MockQuestion q) {
        return MockQuestionResponseDTO.builder()
                .id(q.getId())
                .question(q.getQuestion())
                .point(q.getPoint())
                .questionType(q.getQuestionType())
                .testId(q.getTest() != null ? q.getTest().getId() : null)
                .options(Optional.ofNullable(q.getOptions()).orElse(List.of()).stream()
                        .map(this::toOptionResponse)
                        .toList())
                .answerId(q.getAnswer() != null ? q.getAnswer().getId() : null)
                .createdDate(q.getCreatedDate())
                .updatedDate(q.getUpdatedDate())
                .build();
    }

    private MockOptionResponseDTO toOptionResponse(MockOption o) {
        return MockOptionResponseDTO.builder()
                .id(o.getId())
                .name(o.getName())
                .answer(o.isAnswer())
                .questionId(o.getQuestion() != null ? o.getQuestion().getId() : null)
                .createdDate(o.getCreatedDate())
                .updatedDate(o.getUpdatedDate())
                .build();
    }
}
