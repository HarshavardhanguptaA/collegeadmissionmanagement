package com.internproject.studentms.security;

import com.internproject.studentms.entity.AccountRole;
import com.internproject.studentms.entity.UserAccount;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    @Test
    void issuedTokenCanBeReadAndTamperingIsRejected() {
        JwtService service = new JwtService("test-secret-that-is-long-enough-for-local-tests", 60, new ObjectMapper());
        UserAccount account = new UserAccount();
        account.setId(7L);
        account.setFullName("Test Admin");
        account.setEmail("admin@example.com");
        account.setPasswordHash("not-used-by-this-test");
        account.setRole(AccountRole.ADMIN);
        UserPrincipal principal = new UserPrincipal(account);

        String token = service.createToken(principal);
        assertEquals("admin@example.com", service.subject(token));
        assertNull(service.subject(token + "x"));
    }
}
