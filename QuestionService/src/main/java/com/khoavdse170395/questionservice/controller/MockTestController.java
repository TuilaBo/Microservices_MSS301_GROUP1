package com.khoavdse170395.questionservice.controller;

import com.khoavdse170395.questionservice.model.dto.MockTestDTO;
import com.khoavdse170395.questionservice.service.MockTestService;
import com.khoavdse170395.questionservice.service.MockAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<MockTestDTO>> getAll() {
        return ResponseEntity.ok(mockTestService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mock test by ID", description = "Retrieve a single mock test by its ID.")
    public ResponseEntity<MockTestDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mockTestService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create mock test", description = "Create a new mock test. Provide related entity IDs in the DTO.")
    public ResponseEntity<MockTestDTO> create(@RequestBody MockTestDTO dto) {
        MockTestDTO created = mockTestService.create(dto);
        return ResponseEntity.created(URI.create("/api/mock-tests/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update mock test", description = "Update an existing mock test by ID.")
    public ResponseEntity<MockTestDTO> update(@PathVariable Long id, @RequestBody MockTestDTO dto) {
        return ResponseEntity.ok(mockTestService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete mock test", description = "Delete a mock test by ID.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mockTestService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{testId}/attempts/start")
    @Operation(summary = "Start attempt for test", description = "Create a new attempt for the current user for the given test. Enforces single in-progress attempt per user+test.")
    public ResponseEntity<com.khoavdse170395.questionservice.model.dto.MockAttemptDTO> startAttempt(@PathVariable Long testId) {
        var created = mockAttemptService.startAttempt(testId);
        return ResponseEntity.created(URI.create("/api/mock-attempts/" + created.getId())).body(created);
    }
}

