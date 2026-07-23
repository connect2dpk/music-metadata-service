-- V4__seed_admin_user.sql

-- Insert admin user with password: admin123 (bcrypt hashed)
-- Hash generated with BCryptPasswordEncoder: $2a$10$0R/xqMvMHxHHb6xLEqZWKOO3D3eCiflTmMZr8gOcWm3Yr8CqCvGGe
INSERT INTO users (username, password, email, is_enabled) VALUES
    ('admin', '$2a$10$0R/xqMvMHxHHb6xLEqZWKOO3D3eCiflTmMZr8gOcWm3Yr8CqCvGGe', 'admin@music-service.local', true);

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';



