package com.khoavdse170395.accountservice.controller;

import com.khoavdse170395.accountservice.model.dto.AccountCreateRequest;
import com.khoavdse170395.accountservice.model.dto.AccountResponseDTO;
import com.khoavdse170395.accountservice.model.dto.AccountStatusUpdateRequest;
import com.khoavdse170395.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponseDTO>> getStudents() {
        return ResponseEntity.ok(accountService.getAccountsByRole("STUDENT"));
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponseDTO>> getTeachers() {
        return ResponseEntity.ok(accountService.getAccountsByRole("TEACHER"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> getById(@PathVariable long id) {
        AccountResponseDTO dto = accountService.getAccountDetail(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> create(
            @RequestParam(defaultValue = "STUDENT") String role,
            @Valid @RequestBody AccountCreateRequest req) {
        AccountResponseDTO dto = accountService.createAccountByAdmin(req, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> update(
            @PathVariable long id,
            @RequestParam(required = false) String role,
            @Valid @RequestBody AccountCreateRequest req) {
        AccountResponseDTO dto = accountService.updateAccountByAdmin(id, req, role);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        accountService.deleteAccountByAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> updateStatus(
            @PathVariable long id,
            @RequestBody @Valid AccountStatusUpdateRequest request) {
        AccountResponseDTO dto = accountService.updateAccountStatus(id, request.getActive());
        return ResponseEntity.ok(dto);
    }
}




