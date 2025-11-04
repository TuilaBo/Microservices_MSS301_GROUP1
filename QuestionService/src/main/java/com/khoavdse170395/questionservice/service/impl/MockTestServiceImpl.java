package com.khoavdse170395.questionservice.service.impl;

import com.khoavdse170395.questionservice.model.MockQuestion;
import com.khoavdse170395.questionservice.model.MockTest;
import com.khoavdse170395.questionservice.model.dto.MockOptionDTO;
import com.khoavdse170395.questionservice.model.dto.MockQuestionDTO;
import com.khoavdse170395.questionservice.model.dto.MockTestDTO;
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

    @Override
    public MockTestDTO create(MockTestDTO dto) {
        MockTest entity = new MockTest();
        apply(dto, entity);
        MockTest saved = mockTestRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public MockTestDTO update(Long id, MockTestDTO dto) {
        MockTest entity = mockTestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MockTest not found: " + id));
        apply(dto, entity);
        MockTest saved = mockTestRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        mockTestRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MockTestDTO getById(Long id) {
        return mockTestRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("MockTest not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MockTestDTO> getAll() {
        return mockTestRepository.findAll().stream().map(this::toDTO).toList();
    }

    private void apply(MockTestDTO dto, MockTest entity) {
        entity.setName(dto.getName());
        entity.setDuration(dto.getDurationSeconds() != null ? Duration.ofSeconds(dto.getDurationSeconds()) : null);
        entity.setTotalPoint(dto.getTotalPoint());
        entity.setLessonPlanId(dto.getLessonPlanId());
        entity.setSubscriptionPackageIds(dto.getSubscriptionPackageIds());

        if (dto.getQuestions() != null) {
            List<Long> ids = dto.getQuestions().stream()
                    .map(MockQuestionDTO::getId)
                    .filter(Objects::nonNull)
                    .toList();
            List<MockQuestion> questions = ids.isEmpty() ? new ArrayList<>() : mockQuestionRepository.findAllById(ids);
            entity.setQuestions(questions);
            for (MockQuestion q : questions) {
                q.setTest(entity);
            }
        }
    }

    private MockTestDTO toDTO(MockTest entity) {
        List<MockQuestionDTO> questionDTOs = Optional.ofNullable(entity.getQuestions())
                .map(list -> list.stream().map(this::toQuestionDTO).toList())
                .orElse(List.of());

        return MockTestDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .durationSeconds(entity.getDuration() != null ? entity.getDuration().toSeconds() : null)
                .totalPoint(entity.getTotalPoint())
                .lessonPlanId(entity.getLessonPlanId())
                .subscriptionPackageIds(entity.getSubscriptionPackageIds())
                .questions(questionDTOs)
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .build();
    }

    private MockQuestionDTO toQuestionDTO(MockQuestion q) {
        return MockQuestionDTO.builder()
                .id(q.getId())
                .question(q.getQuestion())
                .point(q.getPoint())
                .questionType(q.getQuestionType())
                .testId(q.getTest() != null ? q.getTest().getId() : null)
                .options(Optional.ofNullable(q.getOptions()).orElse(List.of()).stream()
                        .map(this::toOptionDTO)
                        .toList())
                .answerId(q.getAnswer() != null ? q.getAnswer().getId() : null)
                .createdDate(q.getCreatedDate())
                .updatedDate(q.getUpdatedDate())
                .build();
    }

    private MockOptionDTO toOptionDTO(com.khoavdse170395.questionservice.model.MockOption o) {
        return com.khoavdse170395.questionservice.model.dto.MockOptionDTO.builder()
                .id(o.getId())
                .name(o.getName())
                .answer(o.isAnswer())
                .questionId(o.getQuestion() != null ? o.getQuestion().getId() : null)
                .createdDate(o.getCreatedDate())
                .updatedDate(o.getUpdatedDate())
                .build();
    }
}
