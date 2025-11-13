package com.khoavdse170395.accountservice.service;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.model.PasswordResetToken;
import com.khoavdse170395.accountservice.model.Role;
import com.khoavdse170395.accountservice.model.VerificationCode;
import com.khoavdse170395.accountservice.model.dto.AccountCreateRequest;
import com.khoavdse170395.accountservice.model.dto.AccountResponseDTO;
import com.khoavdse170395.accountservice.repository.AccountRepository;
import com.khoavdse170395.accountservice.repository.PasswordResetTokenRepository;
import com.khoavdse170395.accountservice.repository.RoleRepository;
import com.khoavdse170395.accountservice.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.Objects;

@Service
@FeignClient(name = "account-service", url = "http://localhost:8081")
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public Account addAccount(Account account) {
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new RuntimeException("Username already exists!");
        }
        
        // Validate FPT email domain
        if (!account.getEmail().toLowerCase().endsWith("@fpt.edu.vn")) {
            throw new RuntimeException("Only @fpt.edu.vn email addresses are allowed for registration");
        }
        
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        // Ensure role is set; default to STUDENT if not provided
        if (account.getRole() == null) {
            Role defaultRole = roleRepository.findByRoleName("STUDENT")
                    .orElseThrow(() -> new IllegalStateException("Default role STUDENT is missing"));
            account.setRole(defaultRole);
        }
        // Set account as inactive until email verification
        account.setActive(false);
        
        Account savedAccount = accountRepository.save(account);
        
        // Send verification code
        sendVerificationCode(savedAccount.getEmail());
        
        return savedAccount;
    }

    @Override
    @Transactional
    public Account addAccountWithRole(Account account, String roleName) {
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new RuntimeException("Username already exists!");
        }
        
        // Teacher can register with any email, no FPT domain restriction
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        
        // Set specific role
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
        account.setRole(role);
        
        // Set account as inactive until email verification
        account.setActive(false);
        
        Account savedAccount = accountRepository.save(account);
        
        // Send verification code
        sendVerificationCode(savedAccount.getEmail());
        
        return savedAccount;
    }


    @Override
    public Optional<Account> getAccountById(long accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public void deleteAccount(long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found");
        } else {
            accountRepository.deleteById(accountId);
        }


    }

    @Override
    public Account updateAccount(long id,Account account) {
        if(!accountRepository.existsById(id) ){
            throw new RuntimeException("Account not found");
        } else {
            account.setUserId(id);
            return accountRepository.save(account);
        }
    }

    @Override
    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("User not authenticated");
        }
        
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username);
        
        if (account == null) {
            throw new UsernameNotFoundException("Account not found for username: " + username);
        }
        
        return account;
    }

    @Override
    public AccountResponseDTO getCurrentAccountDTO() {
        Account account = getCurrentAccount();
        return mapToResponseDTO(account);
    }

    @Override
    public Account getAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("Account not found for email: " + email);
        }
        return account;
    }

    @SuppressWarnings("null")
    private AccountResponseDTO mapToResponseDTO(Account account) {
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Account data is missing");
        }
        return AccountResponseDTO.builder()
                .userId(account.getUserId())
                .username(account.getUsername())
                .email(account.getEmail())
                .role(account.getRole())
                .active(account.isActive())
                .createdAt(account.getCreatedAt())
                .fullName(account.getFullName())
                .gender(account.getGender())
                .birthday(account.getBirthday())
                .grade(account.getGrade())
                .build();
    }

    @Override
    @Transactional
    public void sendVerificationCode(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new RuntimeException("Account not found for email: " + email);
        }

        // Generate 6-digit verification code
        String code = generateVerificationCode();
        
        // Delete old unused codes for this account
        verificationCodeRepository.deleteByAccount(account);
        
        // Create new verification code
        VerificationCode verificationCode = VerificationCode.builder()
                .code(code)
                .account(account)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .build();
        verificationCodeRepository.save(Objects.requireNonNull(verificationCode));
        
        // Send email
        emailService.sendVerificationCode(email, code);
    }

    @Override
    @Transactional
    public void verifyAccount(String email, String code) {
        LocalDateTime now = LocalDateTime.now();
        
        // Find account by email
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new RuntimeException("Account not found for email: " + email);
        }
        
        // Find valid verification code
        VerificationCode verificationCode = verificationCodeRepository
                .findByCodeAndAccountAndIsUsedFalseAndExpiresAtAfter(code, account, now)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification code"));
        
        // Mark code as used
        verificationCode.setIsUsed(true);
        verificationCodeRepository.save(verificationCode);
        
        // Activate account
        account.setActive(true);
        accountRepository.save(Objects.requireNonNull(account));
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }

    @Override
    public void validateFptEmailForLogin(String usernameOrEmail) {
        Account account = null;
        
        // Find account by username or email
        if (usernameOrEmail.contains("@")) {
            account = accountRepository.findByEmail(usernameOrEmail);
        } else {
            account = accountRepository.findByUsername(usernameOrEmail);
        }
        
        // If account found, check role - only USER role requires @fpt.edu.vn email
        if (account != null) {
            Role role = account.getRole();
            if (role != null && "USER".equalsIgnoreCase(role.getRoleName())) {
                // USER role must have @fpt.edu.vn email
                String email = account.getEmail();
                if (!email.toLowerCase().endsWith("@fpt.edu.vn")) {
                    throw new RuntimeException("User accounts must use @fpt.edu.vn email addresses for login");
                }
            }
            // Teacher and other roles can use any email - no restriction
        } else {
            // If account not found yet (during registration check), validate email format
            if (usernameOrEmail.contains("@")) {
                // For new registrations, if it's USER role, require @fpt.edu.vn
                // But this is handled in addAccount method, so we can skip here
            }
        }
    }

    @Override
    @Transactional
    public Account processOAuth2Login(String email, String name) {
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            throw new UsernameNotFoundException("Account not registered for email: " + email);
        }

        if (!account.isActive()) {
            throw new IllegalStateException("Account is not active. Please verify your email before using Google login.");
        }

        return account;
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            return;
        }

        Long userId = account.getUserId();
        if (userId != null) {
            passwordResetTokenRepository.deleteByAccount_UserId(userId);
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .account(account)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
        passwordResetTokenRepository.save(Objects.requireNonNull(resetToken));

        emailService.sendPasswordResetEmail(account.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (resetToken.getUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        Account account = resetToken.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(Objects.requireNonNull(account));

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(Objects.requireNonNull(resetToken));
    }

    @Override
    @Transactional
    public AccountResponseDTO updateAccountStatus(Long userId, boolean active) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        }
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        account.setActive(active);
        Account savedAccount = Optional.ofNullable(accountRepository.save(account))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create account"));
        return mapToResponseDTO(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccountsByRole(String roleName) {
        return accountRepository.findAllByRole_RoleNameIgnoreCase(roleName)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountDetail(long userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return mapToResponseDTO(account);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public AccountResponseDTO createAccountByAdmin(AccountCreateRequest request, String roleName) {
        if (accountRepository.findByUsername(request.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (accountRepository.findByEmail(request.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        String effectiveRole = StringUtils.hasText(roleName) ? roleName.toUpperCase() : "STUDENT";
        Role role = roleRepository.findByRoleName(effectiveRole)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + effectiveRole));

        Account account = Account.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .gender(request.getGender())
                .birthday(request.getBirthday())
                .role(role)
                .active(true)
                .build();

        return mapToResponseDTO(Objects.requireNonNull(accountRepository.save(account)));
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public AccountResponseDTO updateAccountByAdmin(long userId, AccountCreateRequest request, String roleName) {
        Account existing = accountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        Account usernameOwner = accountRepository.findByUsername(request.getUsername());
        if (usernameOwner != null && !usernameOwner.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        Account emailOwner = accountRepository.findByEmail(request.getEmail());
        if (emailOwner != null && !emailOwner.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        existing.setUsername(request.getUsername());
        existing.setFullName(request.getFullName());
        if (StringUtils.hasText(request.getPassword())) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        existing.setEmail(request.getEmail());
        existing.setGender(request.getGender());
        existing.setBirthday(request.getBirthday());

        if (StringUtils.hasText(roleName)) {
            Role role = roleRepository.findByRoleName(roleName.toUpperCase())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName));
            existing.setRole(role);
        }

        Account updated = Optional.ofNullable(accountRepository.save(existing))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update account"));
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteAccountByAdmin(long userId) {
        if (userId == 0L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        }
        if (!accountRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        passwordResetTokenRepository.deleteByAccount_UserId(userId);
        accountRepository.deleteById(userId);
    }
}
