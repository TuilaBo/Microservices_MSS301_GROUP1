package com.khoavdse170395.questionservice.controller;

import com.khoavdse170395.questionservice.model.dto.request.MockAnswerRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockAnswerResponseDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockAnswerSubmissionResultDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockAttemptResponseDTO;
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
@RequestMapping("/api/mock-attempts")
@Tag(name = "Mock Attempts", description = "CRUD operations for mock attempts. Relationship fields are represented by IDs in DTOs.")
@RequiredArgsConstructor
public class MockAttemptController {

    private final MockAttemptService mockAttemptService;

    @GetMapping
    @Operation(summary = "List mock attempts", description = "Retrieve all mock attempts as DTOs.")
    public ResponseEntity<List<MockAttemptResponseDTO>> getAll() {
        return ResponseEntity.ok(mockAttemptService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mock attempt by ID", description = "Retrieve a single mock attempt by its ID.")
    public ResponseEntity<MockAttemptResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mockAttemptService.getById(id));
    }

    @GetMapping("/{id}/me")
    @Operation(summary = "Get my mock attempt by ID", description = "Retrieve a mock attempt by its ID only if it belongs to the current authenticated user.")
    public ResponseEntity<MockAttemptResponseDTO> getMyAttemptById(@PathVariable Long id) {
        return ResponseEntity.ok(mockAttemptService.getMyAttemptById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "List my mock attempts", description = "Retrieve all mock attempts for the current authenticated user.")
    public ResponseEntity<List<MockAttemptResponseDTO>> getMyAttempts() {
        return ResponseEntity.ok(mockAttemptService.getMyAttempts());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete mock attempt", description = "Delete a mock attempt by ID.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mockAttemptService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/answers")
    @Operation(summary = "Add answer to attempt", description = "Add an answer to a mock attempt within its active time window.")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MockAnswerSubmissionResultDTO> addAnswer(@PathVariable Long id, @RequestBody MockAnswerRequestDTO dto) {
        MockAnswerSubmissionResultDTO result = mockAttemptService.addAnswer(id, dto);
        if (result.isFinalized()) {
            return ResponseEntity.ok(result);
        }
        MockAnswerResponseDTO answer = result.getAnswer();
        return ResponseEntity.created(URI.create("/api/mock-attempts/" + id + "/answers/" + (answer != null ? answer.getId() : ""))).body(result);
    }

    @PostMapping("/{id}/grade")
    @Operation(summary = "Finalize and grade attempt", description = "Finalize a mock attempt after its end time and compute total points.")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MockAttemptResponseDTO> finalizeAndGrade(@PathVariable Long id) {
        return ResponseEntity.ok(mockAttemptService.finalizeAndGrade(id));
    }
}
