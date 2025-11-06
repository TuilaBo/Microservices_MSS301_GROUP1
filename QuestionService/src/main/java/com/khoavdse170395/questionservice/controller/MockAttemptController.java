package com.khoavdse170395.questionservice.controller;

import com.khoavdse170395.questionservice.model.dto.MockAttemptDTO;
import com.khoavdse170395.questionservice.model.dto.MockAnswerDTO;
import com.khoavdse170395.questionservice.service.MockAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mock-attempts")
@Tag(name = "Mock Attempts", description = "CRUD operations for mock attempts. Relationship fields are represented by IDs in DTOs.")
@RequiredArgsConstructor
public class MockAttemptController {

    private final MockAttemptService mockAttemptService;

    @GetMapping
    @Operation(summary = "List mock attempts", description = "Retrieve all mock attempts as DTOs.")
    public ResponseEntity<List<MockAttemptDTO>> getAll() {
        return ResponseEntity.ok(mockAttemptService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mock attempt by ID", description = "Retrieve a single mock attempt by its ID.")
    public ResponseEntity<MockAttemptDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mockAttemptService.getById(id));
    }

    @GetMapping("/{id}/me")
    @Operation(summary = "Get my mock attempt by ID", description = "Retrieve a mock attempt by its ID only if it belongs to the current authenticated user.")
    public ResponseEntity<MockAttemptDTO> getMyAttemptById(@PathVariable Long id) {
        return ResponseEntity.ok(mockAttemptService.getMyAttemptById(id));
    }

//    @PostMapping
//    @Operation(summary = "Create mock attempt", description = "Create a new mock attempt. Provide related entity IDs in the DTO.")
//    public ResponseEntity<MockAttemptDTO> create(@RequestBody MockAttemptDTO dto) {
//        MockAttemptDTO created = mockAttemptService.create(dto);
//        return ResponseEntity.created(URI.create("/api/mock-attempts/" + created.getId())).body(created);
//    }

//    @PutMapping("/{id}")
//    @Operation(summary = "Update mock attempt", description = "Update an existing mock attempt by ID.")
//    public ResponseEntity<MockAttemptDTO> update(@PathVariable Long id, @RequestBody MockAttemptDTO dto) {
//        return ResponseEntity.ok(mockAttemptService.update(id, dto));
//    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete mock attempt", description = "Delete a mock attempt by ID.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mockAttemptService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/answers")
    @Operation(summary = "Add answer to attempt", description = "Add an answer to a mock attempt within its active time window.")
    public ResponseEntity<MockAnswerDTO> addAnswer(@PathVariable Long id, @RequestBody MockAnswerDTO dto) {
        MockAnswerDTO created = mockAttemptService.addAnswer(id, dto);
        return ResponseEntity.created(URI.create("/api/mock-attempts/" + id + "/answers/" + created.getId())).body(created);
    }

    @PostMapping("/{id}/grade")
    @Operation(summary = "Finalize and grade attempt", description = "Finalize a mock attempt after its end time and compute total points.")
    public ResponseEntity<MockAttemptDTO> finalizeAndGrade(@PathVariable Long id) {
        return ResponseEntity.ok(mockAttemptService.finalizeAndGrade(id));
    }
}
