package com.khoavdse170395.accountservice.controller;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.model.dto.AccountCreateRequest;
import com.khoavdse170395.accountservice.model.dto.JWTAuthResponse;
import com.khoavdse170395.accountservice.model.dto.AccountResponseDTO;
import com.khoavdse170395.accountservice.model.dto.LoginDto;
import com.khoavdse170395.accountservice.security.JwtTokenProvider;
import com.khoavdse170395.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> authenticateUser(
            @RequestBody @Valid LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsernameOrEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTAuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestBody @Valid AccountCreateRequest request) {
        Account account = Account.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .password(request.getPassword()) // Will be encoded in service
                .email(request.getEmail())
                .birthday(request.getBirthday())
                .gender(request.getGender())
                .active(true)
                .build();
        accountService.addAccount(account);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/register-teacher")
    public ResponseEntity<String> registerTeacher(
            @RequestBody @Valid AccountCreateRequest request) {
        Account account = Account.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .password(request.getPassword()) // Will be encoded in service
                .email(request.getEmail())
                .birthday(request.getBirthday())
                .gender(request.getGender())
                .active(true)
                .build();
        accountService.addAccountWithRole(account, "TEACHER");
        return new ResponseEntity<>("Teacher registered successfully!", HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.ok(Collections.singletonMap("authenticated", false));
        }
        return ResponseEntity.ok(Collections.singletonMap("user", principal.getAttributes()));
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> success(@AuthenticationPrincipal OAuth2User principal) {
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "user", principal.getAttributes()
        ));
    }


    @GetMapping("/ping")
    public String success() {
        return "Ok";
    }

    @GetMapping("/get-my-infor")
    public ResponseEntity<AccountResponseDTO> getMyInfor(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = currentUser.getUsername();
        Account account = accountService.getByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + username));

        AccountResponseDTO dto = new AccountResponseDTO(
                account.getUserId(),
                account.getUsername(),
                account.getEmail(),
                account.getRole() != null ? account.getRole().getRoleName() : null,
                account.isActive(),
                account.getCreatedAt(),
                account.getFullName(),
                account.getGender(),
                account.getBirthday(),
                account.getGrade()
        );

        return ResponseEntity.ok(dto);
    }

}

