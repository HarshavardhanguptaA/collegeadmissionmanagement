package com.internproject.studentms.config;

import com.internproject.studentms.entity.AccountRole;
import com.internproject.studentms.entity.UserAccount;
import com.internproject.studentms.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {
    private final UserAccountRepository users;
    private final PasswordEncoder encoder;
    @Value("${app.admin.email:}") private String adminEmail;
    @Value("${app.admin.password:}") private String adminPassword;
    @Value("${app.admin.name:College Administrator}") private String adminName;
    public AdminBootstrapRunner(UserAccountRepository users, PasswordEncoder encoder) { this.users = users; this.encoder = encoder; }
    @Override public void run(ApplicationArguments args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            System.out.println("Admin bootstrap skipped: set APP_ADMIN_EMAIL and APP_ADMIN_PASSWORD to create an admin account.");
            return;
        }
        String normalizedEmail = adminEmail.trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(normalizedEmail)) return;
        UserAccount user = new UserAccount(); user.setFullName(adminName); user.setEmail(normalizedEmail);
        user.setPasswordHash(encoder.encode(adminPassword)); user.setRole(AccountRole.ADMIN); users.save(user);
        System.out.println("Initial administrator account created from environment configuration.");
    }
}
