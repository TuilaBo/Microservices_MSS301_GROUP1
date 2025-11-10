package com.khoavdse170395.questionservice.service.impl;

import com.khoavdse170395.questionservice.model.MockOption;
import com.khoavdse170395.questionservice.model.MockQuestion;
import com.khoavdse170395.questionservice.model.MockTest;
import com.khoavdse170395.questionservice.model.dto.request.MockQuestionRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockOptionResponseDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockQuestionResponseDTO;
import com.khoavdse170395.questionservice.repository.MockOptionRepository;
import com.khoavdse170395.questionservice.repository.MockQuestionRepository;
import com.khoavdse170395.questionservice.repository.MockTestRepository;
import com.khoavdse170395.questionservice.service.MockQuestionService;
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
public class MockQuestionServiceImpl implements MockQuestionService {

    private final MockQuestionRepository mockQuestionRepository;
    private final MockTestRepository mockTestRepository;
    private final MockOptionRepository mockOptionRepository;

    @Override
    public MockQuestionResponseDTO create(MockQuestionRequestDTO dto) {
        MockQuestion entity = new MockQuestion();
        apply(dto, entity);
        MockQuestion saved = mockQuestionRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public MockQuestionResponseDTO update(Long id, MockQuestionRequestDTO dto) {
        MockQuestion entity = mockQuestionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MockQuestion not found: " + id));
        apply(dto, entity);
        MockQuestion saved = mockQuestionRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        mockQuestionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MockQuestionResponseDTO getById(Long id) {
        return mockQuestionRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("MockQuestion not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MockQuestionResponseDTO> getAll() {
        return mockQuestionRepository.findAll().stream().map(this::toResponse).toList();
    }

    private void apply(MockQuestionRequestDTO dto, MockQuestion entity) {
        entity.setQuestion(dto.getQuestion());
        entity.setPoint(dto.getPoint());
        entity.setQuestionType(dto.getQuestionType());

        if (dto.getTestId() != null) {
            MockTest test = mockTestRepository.findById(dto.getTestId())
                    .orElseThrow(() -> new IllegalArgumentException("MockTest not found: " + dto.getTestId()));
            entity.setTest(test);
        } else {
            entity.setTest(null);
        }

        if (dto.getOptionIds() != null) {
            List<Long> optionIds = dto.getOptionIds().stream()
                    .filter(Objects::nonNull)
                    .toList();
            List<MockOption> options = optionIds.isEmpty() ? new ArrayList<>() : mockOptionRepository.findAllById(optionIds);
            entity.setOptions(options);
        }

        if (dto.getAnswerId() != null) {
            MockOption answer = mockOptionRepository.findById(dto.getAnswerId())
                    .orElseThrow(() -> new IllegalArgumentException("MockOption not found: " + dto.getAnswerId()));
            entity.setAnswer(answer);
        } else {
            entity.setAnswer(null);
        }
    }

    private MockQuestionResponseDTO toResponse(MockQuestion entity) {
        List<MockOptionResponseDTO> optionDTOs = Optional.ofNullable(entity.getOptions())
                .map(list -> list.stream().map(this::toOptionResponse).toList())
                .orElse(List.of());

        return MockQuestionResponseDTO.builder()
                .id(entity.getId())
                .question(entity.getQuestion())
                .point(entity.getPoint())
                .questionType(entity.getQuestionType())
                .testId(entity.getTest() != null ? entity.getTest().getId() : null)
                .options(optionDTOs)
                .answerId(entity.getAnswer() != null ? entity.getAnswer().getId() : null)
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
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
