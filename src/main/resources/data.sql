-- =============================================================================
-- E-Commerce Test Data
-- =============================================================================

-- Seed roles
INSERT INTO roles (role_name) VALUES ('ROLE_CUSTOMER') ON CONFLICT DO NOTHING;
INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;

