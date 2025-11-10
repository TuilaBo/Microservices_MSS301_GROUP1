package com.khoavdse170395.questionservice.controller;

import com.khoavdse170395.questionservice.model.dto.request.MockTestRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockAttemptResponseDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockTestResponseDTO;
import com.khoavdse170395.questionservice.service.MockTestService;
import com.khoavdse170395.questionservice.service.MockAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mock-tests")
@Tag(name = "Mock Tests", description = "CRUD operations for mock tests. Relationship fields are represented by IDs in DTOs.")
@RequiredArgsConstructor
public class MockTestController {

    private final MockTestService mockTestService;
    private final MockAttemptService mockAttemptService;

    @GetMapping
    @Operation(summary = "List mock tests", description = "Retrieve all mock tests as DTOs.")
    public ResponseEntity<List<MockTestResponseDTO>> getAll() {
        return ResponseEntity.ok(mockTestService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mock test by ID", description = "Retrieve a single mock test by its ID.")
    public ResponseEntity<MockTestResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mockTestService.getById(id));
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "List mock tests by lesson", description = "Retrieve mock tests associated with the given lesson ID.")
    public ResponseEntity<List<MockTestResponseDTO>> getByLesson(@PathVariable String lessonId) {
        return ResponseEntity.ok(mockTestService.getByLessonId(lessonId));
    }

    @PostMapping
    @Operation(summary = "Create mock test", description = "Create a new mock test. Provide related entity IDs in the DTO.")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MockTestResponseDTO> create(@RequestBody MockTestRequestDTO dto) {
        MockTestResponseDTO created = mockTestService.create(dto);
        return ResponseEntity.created(URI.create("/api/mock-tests/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update mock test", description = "Update an existing mock test by ID.")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MockTestResponseDTO> update(@PathVariable Long id, @RequestBody MockTestRequestDTO dto) {
        return ResponseEntity.ok(mockTestService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete mock test", description = "Delete a mock test by ID.")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mockTestService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{testId}/attempts/start")
    @Operation(summary = "Start attempt for test", description = "Create a new attempt for the current user for the given test. Enforces single in-progress attempt per user+test.")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MockAttemptResponseDTO> startAttempt(@PathVariable Long testId) {
        var created = mockAttemptService.startAttempt(testId);
        return ResponseEntity.created(URI.create("/api/mock-attempts/" + created.getId())).body(created);
    }
}

