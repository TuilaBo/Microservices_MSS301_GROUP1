package com.khoavdse170395.accountservice.repository;

import com.khoavdse170395.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
    Account findByUsernameOrEmail(String username, String email);
    Account findByEmail(String email);

    List<Account> findAllByRole_RoleNameIgnoreCase(String roleName);
}
