package com.deepak.music;

import com.deepak.music.common.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        // Keep one container alive for the complete test run.
        // This prevents Spring context caching from holding a stale mapped port.
        postgres.start();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwt.secret:mySecretKeyForJWTTokenGenerationAndValidationPurposeOnly12345678}")
    protected String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    protected long jwtExpirationMs;

    /**
     * Generates a JWT token for the admin user for authentication in tests.
     * Admin user is pre-seeded in the database with username "admin".
     */
    protected String generateAdminToken() {
        return generateToken("admin");
    }

    /**
     * Generates a JWT token for a given username.
     */
    protected String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

}

