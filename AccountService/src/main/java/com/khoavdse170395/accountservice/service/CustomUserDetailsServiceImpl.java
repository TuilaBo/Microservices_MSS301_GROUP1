package com.khoavdse170395.accountservice.service;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.repository.AccountRepository;
import com.khoavdse170395.accountservice.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        Account account = accountRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (account == null) {
            account = accountRepository.findByEmail(usernameOrEmail);
        }
        if (account == null) {
            throw new UsernameNotFoundException("User not found: " + usernameOrEmail);
        }
        return new CustomUserDetails(account);
    }
}
