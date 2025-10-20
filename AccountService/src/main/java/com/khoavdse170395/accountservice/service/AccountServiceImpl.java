package com.khoavdse170395.accountservice.service;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.model.Role;
import com.khoavdse170395.accountservice.repository.AccountRepository;
import com.khoavdse170395.accountservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FeignClient(name = "account-service", url = "http://localhost:8081")
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
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
        return accountRepository.save(account);
    }

    @Override
    public Account addAccountWithRole(Account account, String roleName) {
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new RuntimeException("Username already exists!");
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        
        // Set specific role
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
        account.setRole(role);
        
        return accountRepository.save(account);
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
}
