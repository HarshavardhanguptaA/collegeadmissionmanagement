package com.internproject.studentms.security;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtService {
    private static final String HEADER = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
    private final byte[] secret;
    private final long expirationMinutes;
    private final ObjectMapper mapper;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes:240}") long expirationMinutes,
                      ObjectMapper mapper) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) {
            throw new IllegalArgumentException("APP_JWT_SECRET must contain at least 32 bytes");
        }
        this.expirationMinutes = expirationMinutes;
        this.mapper = mapper;
    }

    public String createToken(UserPrincipal principal) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", principal.getUsername());
            payload.put("role", principal.getRole());
            payload.put("exp", Instant.now().plusSeconds(expirationMinutes * 60).getEpochSecond());
            String body = Base64.getUrlEncoder().withoutPadding().encodeToString(mapper.writeValueAsBytes(payload));
            String signingInput = HEADER + "." + body;
            return signingInput + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(sign(signingInput));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not issue authentication token", ex);
        }
    }

    public String subject(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            byte[] provided = Base64.getUrlDecoder().decode(parts[2]);
            if (!MessageDigest.isEqual(provided, sign(parts[0] + "." + parts[1]))) return null;
            Map<String, Object> claims = mapper.readValue(Base64.getUrlDecoder().decode(parts[1]), new TypeReference<>() {});
            Object expiration = claims.get("exp");
            Object sub = claims.get("sub");
            if (!(expiration instanceof Number exp) || exp.longValue() <= Instant.now().getEpochSecond()) return null;
            return sub instanceof String email ? email : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private byte[] sign(String input) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
    }
}
