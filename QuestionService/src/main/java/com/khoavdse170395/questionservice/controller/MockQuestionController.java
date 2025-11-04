package com.khoavdse170395.questionservice.controller;

import com.khoavdse170395.questionservice.model.dto.MockQuestionDTO;
import com.khoavdse170395.questionservice.service.MockQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mock-questions")
@Tag(name = "Mock Questions", description = "CRUD operations for mock questions. Relationship fields are represented by IDs in DTOs.")
@RequiredArgsConstructor
public class MockQuestionController {

    private final MockQuestionService mockQuestionService;

    @GetMapping
    @Operation(summary = "List mock questions", description = "Retrieve all mock questions as DTOs.")
    public ResponseEntity<List<MockQuestionDTO>> getAll() {
        return ResponseEntity.ok(mockQuestionService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mock question by ID", description = "Retrieve a single mock question by its ID.")
    public ResponseEntity<MockQuestionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mockQuestionService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create mock question", description = "Create a new mock question. Provide related entity IDs in the DTO.")
    public ResponseEntity<MockQuestionDTO> create(@RequestBody MockQuestionDTO dto) {
        MockQuestionDTO created = mockQuestionService.create(dto);
        return ResponseEntity.created(URI.create("/api/mock-questions/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update mock question", description = "Update an existing mock question by ID.")
    public ResponseEntity<MockQuestionDTO> update(@PathVariable Long id, @RequestBody MockQuestionDTO dto) {
        return ResponseEntity.ok(mockQuestionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete mock question", description = "Delete a mock question by ID.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mockQuestionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

