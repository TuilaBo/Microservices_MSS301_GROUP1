package com.khoavdse170395.accountservice.controller;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.model.dto.AccountCreateRequest;
import com.khoavdse170395.accountservice.model.dto.AccountResponseDTO;
import com.khoavdse170395.accountservice.model.dto.JWTAuthResponse;
import com.khoavdse170395.accountservice.model.dto.LoginDto;
import com.khoavdse170395.accountservice.model.dto.ResendCodeRequest;
import com.khoavdse170395.accountservice.model.dto.VerifyRequest;
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
        // Validate FPT email domain before authentication
        accountService.validateFptEmailForLogin(loginDto.getUsernameOrEmail());
        
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
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication failed"));
        }
        
        // Get email from OAuth2 user
        String email = principal.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email not found in OAuth2 user"));
        }
        
        // Get account from service by email
        Account account = accountService.getAccountByEmail(email);
        
        // Create authentication for JWT token generation
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                principal.getAuthorities()
        );
        
        // Generate JWT token
        String token = tokenProvider.generateToken(authentication);
        
        // Return token and user info
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "token", token,
                "user", Map.of(
                        "userId", account.getUserId(),
                        "username", account.getUsername(),
                        "email", account.getEmail(),
                        "fullName", account.getFullName(),
                        "role", account.getRole().getRoleName()
                )
        ));
    }


    @GetMapping("/me")
    public ResponseEntity<AccountResponseDTO> getMe() {
        AccountResponseDTO accountDTO = accountService.getCurrentAccountDTO();
        return ResponseEntity.ok(accountDTO);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestBody @Valid VerifyRequest request) {
        accountService.verifyAccount(request.getEmail(), request.getCode());
        return new ResponseEntity<>("Account verified successfully!", HttpStatus.OK);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<String> resendVerificationCode(@RequestBody @Valid ResendCodeRequest request) {
        accountService.sendVerificationCode(request.getEmail());
        return new ResponseEntity<>("Verification code sent to your email!", HttpStatus.OK);
    }

    @GetMapping("/google")
    public ResponseEntity<Map<String, String>> googleLogin() {
        return ResponseEntity.ok(Map.of(
                "message", "Redirect to /oauth2/authorization/google to start Google OAuth2 login",
                "url", "/oauth2/authorization/google"
        ));
    }

    @GetMapping("/ping")
    public String success() {
        return "Ok";
    }

}

