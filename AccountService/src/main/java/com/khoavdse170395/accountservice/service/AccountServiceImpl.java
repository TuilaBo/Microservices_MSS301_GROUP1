package com.khoavdse170395.accountservice.service;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.model.Role;
import com.khoavdse170395.accountservice.model.VerificationCode;
import com.khoavdse170395.accountservice.model.dto.AccountResponseDTO;
import com.khoavdse170395.accountservice.repository.AccountRepository;
import com.khoavdse170395.accountservice.repository.RoleRepository;
import com.khoavdse170395.accountservice.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        // Ensure role is set; default to USER if not provided
        if (account.getRole() == null) {
            Role defaultRole = roleRepository.findByRoleName("USER")
                    .orElseThrow(() -> new IllegalStateException("Default role USER is missing"));
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
        if(!accountRepository.existsById(account.getUserId()) ){
            throw new RuntimeException("Account not found");
        } else {
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

    private AccountResponseDTO mapToResponseDTO(Account account) {
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
        
        verificationCodeRepository.save(verificationCode);
        
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
        Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository
                .findByCodeAndAccountAndIsUsedFalseAndExpiresAtAfter(code, account, now);
        
        if (verificationCodeOpt.isEmpty()) {
            throw new RuntimeException("Invalid or expired verification code");
        }
        
        VerificationCode verificationCode = verificationCodeOpt.get();
        
        // Mark code as used
        verificationCode.setIsUsed(true);
        verificationCodeRepository.save(verificationCode);
        
        // Activate account
        account.setActive(true);
        accountRepository.save(account);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }
}
