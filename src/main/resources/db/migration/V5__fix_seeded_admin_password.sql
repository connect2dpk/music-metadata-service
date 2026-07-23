-- V5__fix_seeded_admin_password.sql

-- Keep admin login deterministic for existing databases created before the V4 hash fix.
UPDATE users
SET password = '$2a$10$SvcmKjobf7EpCsc86dWpAu24PKM4/OrFiuQ.nPvTevdIXkvlOYGUq'
WHERE username = 'admin';

