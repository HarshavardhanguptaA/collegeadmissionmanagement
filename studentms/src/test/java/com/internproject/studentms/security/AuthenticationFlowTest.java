package com.internproject.studentms.security;

import com.internproject.studentms.config.AdminBootstrapRunner;
import com.internproject.studentms.dto.AuthResponse;
import com.internproject.studentms.dto.LoginRequest;
import com.internproject.studentms.entity.AccountRole;
import com.internproject.studentms.entity.UserAccount;
import com.internproject.studentms.repository.UserAccountRepository;
import com.internproject.studentms.service.AuthService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthenticationFlowTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminBootstrapRunner adminBootstrapRunner;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        userAccountRepository.deleteAll();
    }

    @Test
    void testLoginRequestValidationWithConfiguredEmail() {
        LoginRequest validRequest = new LoginRequest("admin@college.local", "CollegeAdmin@2026!");
        var violations = validator.validate(validRequest);
        assertTrue(violations.isEmpty(), "admin@college.local should pass LoginRequest Bean Validation");
    }

    @Test
    void testAdminBootstrapCreatesAccountAndAllowsLogin() {
        ReflectionTestUtils.setField(adminBootstrapRunner, "adminEmail", "admin@college.local");
        ReflectionTestUtils.setField(adminBootstrapRunner, "adminPassword", "CollegeAdmin@2026!");
        ReflectionTestUtils.setField(adminBootstrapRunner, "adminName", "College Administrator");

        adminBootstrapRunner.run(new DefaultApplicationArguments());

        UserAccount admin = userAccountRepository.findByEmailIgnoreCase("admin@college.local").orElse(null);
        assertNotNull(admin, "Admin account should be created");
        assertEquals(AccountRole.ADMIN, admin.getRole());
        assertTrue(passwordEncoder.matches("CollegeAdmin@2026!", admin.getPasswordHash()));

        AuthResponse response = authService.login(new LoginRequest("admin@college.local", "CollegeAdmin@2026!"));
        assertNotNull(response.accessToken());
        assertEquals("ADMIN", response.role());
        assertEquals("admin@college.local", response.email());
    }

    @Test
    void testAdminBootstrapResetsPasswordIfAdminAlreadyExistsWithOldPassword() {
        // Pre-create an admin with old password
        UserAccount oldAdmin = new UserAccount();
        oldAdmin.setFullName("College Administrator");
        oldAdmin.setEmail("admin@college.local");
        oldAdmin.setPasswordHash(passwordEncoder.encode("OldWrongPassword!123"));
        oldAdmin.setRole(AccountRole.ADMIN);
        userAccountRepository.save(oldAdmin);

        ReflectionTestUtils.setField(adminBootstrapRunner, "adminEmail", "admin@college.local");
        ReflectionTestUtils.setField(adminBootstrapRunner, "adminPassword", "CollegeAdmin@2026!");
        ReflectionTestUtils.setField(adminBootstrapRunner, "adminName", "College Administrator");

        adminBootstrapRunner.run(new DefaultApplicationArguments());

        UserAccount updatedAdmin = userAccountRepository.findByEmailIgnoreCase("admin@college.local").orElse(null);
        assertNotNull(updatedAdmin);
        // Note: Currently in existing code this will FAIL because existing code just returns without updating!
        assertTrue(passwordEncoder.matches("CollegeAdmin@2026!", updatedAdmin.getPasswordHash()),
                "Admin password should be updated to configured password");

        AuthResponse response = authService.login(new LoginRequest("admin@college.local", "CollegeAdmin@2026!"));
        assertNotNull(response.accessToken());
    }
}
