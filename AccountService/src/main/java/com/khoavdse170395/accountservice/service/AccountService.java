package com.khoavdse170395.accountservice.service;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.model.dto.AccountCreateRequest;
import com.khoavdse170395.accountservice.model.dto.AccountResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AccountService {
    List<Account> getAllAccounts();
    Account addAccount(Account account);
    Account addAccountWithRole(Account account, String roleName);
    Optional<Account> getAccountById(long accountId);
    void deleteAccount(long accountId);
    Account updateAccount(long id,Account account);
    Account getCurrentAccount();
    AccountResponseDTO getCurrentAccountDTO();
    Account getAccountByEmail(String email);
    void sendVerificationCode(String email);
    void verifyAccount(String email, String code);
    void validateFptEmailForLogin(String usernameOrEmail);
    Account processOAuth2Login(String email, String name);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
    AccountResponseDTO updateAccountStatus(Long userId, boolean active);
    List<AccountResponseDTO> getAccountsByRole(String roleName);
    AccountResponseDTO getAccountDetail(long userId);
    AccountResponseDTO createAccountByAdmin(AccountCreateRequest request, String roleName);
    AccountResponseDTO updateAccountByAdmin(long userId, AccountCreateRequest request, String roleName);
    void deleteAccountByAdmin(long userId);
}
