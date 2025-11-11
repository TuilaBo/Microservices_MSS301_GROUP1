package com.khoavdse170395.questionservice.controller;

import com.khoavdse170395.questionservice.model.dto.request.MockOptionRequestDTO;
import com.khoavdse170395.questionservice.model.dto.request.MockQuestionRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockOptionResponseDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockQuestionResponseDTO;
import com.khoavdse170395.questionservice.service.MockOptionService;
import com.khoavdse170395.questionservice.service.MockQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mock-questions")
@Tag(name = "Mock Questions", description = "CRUD operations for mock questions. Relationship fields are represented by IDs in DTOs.")
@RequiredArgsConstructor
public class MockQuestionController {

    private final MockQuestionService mockQuestionService;
    private final MockOptionService mockOptionService;

    @GetMapping
    @Operation(summary = "List mock questions", description = "Retrieve all mock questions as DTOs.")
    public ResponseEntity<List<MockQuestionResponseDTO>> getAll() {
        return ResponseEntity.ok(mockQuestionService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mock question by ID", description = "Retrieve a single mock question by its ID.")
    public ResponseEntity<MockQuestionResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mockQuestionService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create mock question", description = "Create a new mock question. Provide related entity IDs in the DTO.")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MockQuestionResponseDTO> create(@RequestBody MockQuestionRequestDTO dto) {
        MockQuestionResponseDTO created = mockQuestionService.create(dto);
        return ResponseEntity.created(URI.create("/api/mock-questions/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update mock question", description = "Update an existing mock question by ID.")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MockQuestionResponseDTO> update(@PathVariable Long id, @RequestBody MockQuestionRequestDTO dto) {
        return ResponseEntity.ok(mockQuestionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete mock question", description = "Delete a mock question by ID.")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mockQuestionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{questionId}/answers")
    @Operation(summary = "List answers for a question", description = "Retrieve all answer options for the specified question.")
    public ResponseEntity<List<MockOptionResponseDTO>> getAnswers(@PathVariable Long questionId) {
        return ResponseEntity.ok(mockOptionService.getByQuestionId(questionId));
    }

    @PostMapping("/{questionId}/answers")
    @Operation(summary = "Create answer for a question", description = "Add a new answer option to the specified question.")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MockOptionResponseDTO> createAnswer(@PathVariable Long questionId,
                                                              @RequestBody MockOptionRequestDTO dto) {
        MockOptionResponseDTO created = mockOptionService.createForQuestion(questionId, dto);
        return ResponseEntity
                .created(URI.create("/api/mock-questions/" + questionId + "/answers/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{questionId}/answers/{answerId}")
    @Operation(summary = "Update answer for a question", description = "Update an existing answer option for the specified question.")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MockOptionResponseDTO> updateAnswer(@PathVariable Long questionId,
                                                              @PathVariable Long answerId,
                                                              @RequestBody MockOptionRequestDTO dto) {
        return ResponseEntity.ok(mockOptionService.updateForQuestion(questionId, answerId, dto));
    }
}

