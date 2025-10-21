package com.khoavdse170395.accountservice.config;

import com.khoavdse170395.accountservice.model.Gender;
import com.khoavdse170395.accountservice.model.Role;
import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.repository.RoleRepository;
import com.khoavdse170395.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Check if data already exists
        if (accountRepository.count() > 0) {
            log.info("Data already exists, skipping initialization.");
            return;
        }

        // Create roles first
        createRoles();
        
        // Create sample accounts
        createSampleAccounts();
        
        log.info("Data initialization completed successfully!");
    }

    private void createRoles() {
        // Admin Role
        Role adminRole = Role.builder()
                .roleName("ADMIN")
                .description("System Administrator")
                .isActive(true)
                .build();

        // User Role
        Role userRole = Role.builder()
                .roleName("USER")
                .description("Regular User")
                .isActive(true)
                .build();

        // Student Role
        Role studentRole = Role.builder()
                .roleName("STUDENT")
                .description("Student User")
                .isActive(true)
                .build();

        // Teacher Role
        Role teacherRole = Role.builder()
                .roleName("TEACHER")
                .description("Teacher User")
                .isActive(true)
                .build();

        // Save roles
        roleRepository.save(adminRole);
        roleRepository.save(userRole);
        roleRepository.save(studentRole);
        roleRepository.save(teacherRole);

        log.info("Created {} roles", 4);
    }

    private void createSampleAccounts() {
        // Get roles
        Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();
        Role studentRole = roleRepository.findByRoleName("STUDENT").orElseThrow();
        Role teacherRole = roleRepository.findByRoleName("TEACHER").orElseThrow();
        // Admin Account
        Account admin = Account.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@accountservice.com")
                .role(adminRole)
                .active(true)
                .fullName("System Administrator")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1990, 1, 15))
                .grade("ADMIN")
                .createdAt(LocalDateTime.now())
                .build();

        // Regular Account


        // Female Account


        // Student Account
        Account student = Account.builder()
                .username("student01")
                .password(passwordEncoder.encode("student123"))
                .email("student01@school.com")
                .role(studentRole)
                .active(true)
                .fullName("Student One")
                .gender(Gender.OTHER)
                .birthday(LocalDate.of(2000, 12, 25))
                .grade("GRADE_10")
                .createdAt(LocalDateTime.now())
                .build();

        // Teacher Account
        Account teacher = Account.builder()
                .username("teacher01")
                .password(passwordEncoder.encode("teacher123"))
                .email("teacher01@school.com")
                .role(teacherRole)
                .active(true)
                .fullName("Teacher One")
                .gender(Gender.FEMALE)
                .birthday(LocalDate.of(1985, 6, 15))
                .grade("PROFESSOR")
                .createdAt(LocalDateTime.now())
                .build();

        // Inactive Account

        // Save all accounts
        accountRepository.save(admin);
        accountRepository.save(student);
        accountRepository.save(teacher);
        log.info("Created {} sample accounts", 6);
        log.info("Sample accounts created:");
        log.info("- Admin: admin/admin123");
        log.info("- User 1: john_doe/password123");
        log.info("- User 2: jane_smith/password123");
        log.info("- Student: student01/student123");
        log.info("- Teacher: teacher01/teacher123");
        log.info("- Inactive: inactive_user/password123");
    }
}

