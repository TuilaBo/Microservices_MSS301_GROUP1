package com.khoavdse170395.accountservice.security;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Account account;

    public CustomUserDetails(Account account) {
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = account.getRole();
        if (role != null && role.getRoleName() != null) {
            return Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role.getRoleName().toUpperCase())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return account.isActive();
    }
}
