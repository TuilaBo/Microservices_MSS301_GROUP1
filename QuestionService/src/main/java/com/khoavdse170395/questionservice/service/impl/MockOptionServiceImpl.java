package com.khoavdse170395.questionservice.service.impl;

import com.khoavdse170395.questionservice.model.MockOption;
import com.khoavdse170395.questionservice.model.MockQuestion;
import com.khoavdse170395.questionservice.model.dto.request.MockOptionRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockOptionResponseDTO;
import com.khoavdse170395.questionservice.repository.MockOptionRepository;
import com.khoavdse170395.questionservice.repository.MockQuestionRepository;
import com.khoavdse170395.questionservice.service.MockOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MockOptionServiceImpl implements MockOptionService {

    private final MockQuestionRepository mockQuestionRepository;
    private final MockOptionRepository mockOptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MockOptionResponseDTO> getByQuestionId(Long questionId) {
        MockQuestion question = findQuestion(questionId);
        return mockOptionRepository.findByQuestion_Id(question.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MockOptionResponseDTO createForQuestion(Long questionId, MockOptionRequestDTO dto) {
        MockQuestion question = findQuestion(questionId);

        MockOption option = MockOption.builder()
                .name(dto.getName())
                .isAnswer(dto.isAnswer())
                .question(question)
                .build();

        MockOption saved = mockOptionRepository.save(option);
        syncQuestionAnswer(question, saved, dto.isAnswer());

        return toResponse(saved);
    }

    @Override
    public MockOptionResponseDTO updateForQuestion(Long questionId, Long optionId, MockOptionRequestDTO dto) {
        MockQuestion question = findQuestion(questionId);
        MockOption option = mockOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("MockOption not found: " + optionId));

        if (option.getQuestion() == null || !option.getQuestion().getId().equals(question.getId())) {
            throw new IllegalArgumentException("MockOption does not belong to question: " + questionId);
        }

        option.setName(dto.getName());
        option.setAnswer(dto.isAnswer());

        MockOption saved = mockOptionRepository.save(option);
        syncQuestionAnswer(question, saved, dto.isAnswer());

        return toResponse(saved);
    }

    private MockQuestion findQuestion(Long questionId) {
        return mockQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("MockQuestion not found: " + questionId));
    }

    private void syncQuestionAnswer(MockQuestion question, MockOption option, boolean isAnswer) {
        if (isAnswer) {
            question.setAnswer(option);
            mockQuestionRepository.save(question);
        } else if (question.getAnswer() != null && question.getAnswer().getId().equals(option.getId())) {
            question.setAnswer(null);
            mockQuestionRepository.save(question);
        }
    }

    private MockOptionResponseDTO toResponse(MockOption option) {
        return MockOptionResponseDTO.builder()
                .id(option.getId())
                .name(option.getName())
                .answer(option.isAnswer())
                .questionId(option.getQuestion() != null ? option.getQuestion().getId() : null)
                .createdDate(option.getCreatedDate())
                .updatedDate(option.getUpdatedDate())
                .build();
    }
}
