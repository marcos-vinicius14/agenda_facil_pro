-- ============================================
-- ORGANIZATIONS
-- ============================================
CREATE TABLE tb_organizations
(
    id                UUID PRIMARY KEY,
    name              VARCHAR(255)       NOT NULL,
    document          VARCHAR(20) UNIQUE NOT NULL,
    subscription_tier VARCHAR(20)        NOT NULL DEFAULT 'BASIC',
    created_at        TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_organizations_document ON tb_organizations (document);
CREATE INDEX idx_organizations_tier ON tb_organizations (subscription_tier);

-- ============================================
-- USERS (CREDENTIALS ONLY)
-- ============================================
CREATE TABLE tb_users
(
    id              UUID PRIMARY KEY,
    organization_id UUID         NOT NULL REFERENCES tb_organizations (id) ON DELETE CASCADE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    failed_attempts INTEGER      NOT NULL DEFAULT 0,
    lockout_time    TIMESTAMP,
    created_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_users_email ON tb_users (email);
CREATE INDEX idx_users_org ON tb_users (organization_id);
CREATE INDEX idx_users_enabled ON tb_users (enabled);

-- ============================================
-- PROFILES (DENTIST, SECRETARY)
-- ============================================
CREATE TABLE tb_profiles
(
    id         UUID PRIMARY KEY,
    user_id    UUID         NOT NULL UNIQUE REFERENCES tb_users (id) ON DELETE CASCADE,
    name       VARCHAR(255) NOT NULL,
    phone      VARCHAR(20),
    role_type  VARCHAR(30)  NOT NULL,
    is_manager BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_profiles_user ON tb_profiles (user_id);
CREATE INDEX idx_profiles_role ON tb_profiles (role_type);

-- ============================================
-- ROLES
-- ============================================
CREATE TABLE tb_roles
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

INSERT INTO tb_roles (id, name, description)
VALUES ('0193512b-ccf0-7000-8000-000000000001', 'ADMIN', 'System Administrator with full access'),
       ('0193512b-ccf0-7000-8000-000000000002', 'DENTIST', 'Dentist owner of clinic with management permissions'),
       ('0193512b-ccf0-7000-8000-000000000003', 'SECRETARY', 'Secretary with operational permissions');

-- ============================================
-- PERMISSIONS (GRANULAR)
-- ============================================
CREATE TABLE tb_permissions
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

INSERT INTO tb_permissions (id, name, description)
VALUES ('0193512b-ccf0-7000-9000-000000000001', 'ORG_READ', 'Read organization details'),
       ('0193512b-ccf0-7000-9000-000000000002', 'ORG_UPDATE', 'Update organization settings'),
       ('0193512b-ccf0-7000-9000-000000000003', 'PATIENT_CREATE', 'Create new patients'),
       ('0193512b-ccf0-7000-9000-000000000004', 'PATIENT_READ_ALL', 'Read all patients from organization'),
       ('0193512b-ccf0-7000-9000-000000000005', 'APPOINTMENT_CREATE', 'Create appointments'),
       ('0193512b-ccf0-7000-9000-000000000006', 'APPOINTMENT_RESCHEDULE_OWN', 'Reschedule own appointments only'),
       ('0193512b-ccf0-7000-9000-000000000007', 'APPOINTMENT_RESCHEDULE_ANY', 'Reschedule any appointment'),
       ('0193512b-ccf0-7000-9000-000000000008', 'APPOINTMENT_CANCEL_OWN', 'Cancel own appointments only'),
       ('0193512b-ccf0-7000-9000-000000000009', 'APPOINTMENT_CANCEL_ANY', 'Cancel any appointment'),
       ('0193512b-ccf0-7000-9000-000000000010', 'FINANCIAL_VIEW', 'View financial reports (Dentist only)');

-- ============================================
-- ROLE_PERMISSIONS (MANY-TO-MANY)
-- ============================================
CREATE TABLE tb_role_permissions
(
    role_id       UUID NOT NULL REFERENCES tb_roles (id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES tb_permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);


-- DENTIST (ID ...0002) gets ALL permissions currently registered
INSERT INTO tb_role_permissions (role_id, permission_id)
SELECT '0193512b-ccf0-7000-8000-000000000002', id FROM tb_permissions;

-- SECRETARY (ID ...0003) gets operational permissions only
INSERT INTO tb_role_permissions (role_id, permission_id)
VALUES
    ('0193512b-ccf0-7000-8000-000000000003', '0193512b-ccf0-7000-9000-000000000003'), -- PATIENT_CREATE
    ('0193512b-ccf0-7000-8000-000000000003', '0193512b-ccf0-7000-9000-000000000004'), -- PATIENT_READ_ALL
    ('0193512b-ccf0-7000-8000-000000000003', '0193512b-ccf0-7000-9000-000000000005'), -- APPOINTMENT_CREATE
    ('0193512b-ccf0-7000-8000-000000000003', '0193512b-ccf0-7000-9000-000000000007'), -- APPOINTMENT_RESCHEDULE_ANY
    ('0193512b-ccf0-7000-8000-000000000003', '0193512b-ccf0-7000-9000-000000000009'); -- APPOINTMENT_CANCEL_ANY

-- ============================================
-- USER_ROLES (MANY-TO-MANY)
-- ============================================
CREATE TABLE tb_user_roles
(
    user_id UUID NOT NULL REFERENCES tb_users (id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES tb_roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON tb_user_roles (user_id);

-- ============================================
-- AUDIT LOG
-- ============================================
CREATE TABLE tb_audit_log
(
    id              UUID PRIMARY KEY,
    organization_id UUID         NOT NULL REFERENCES tb_organizations (id),
    user_id         UUID         NOT NULL REFERENCES tb_users (id),
    entity_type     VARCHAR(50)  NOT NULL,
    entity_id       VARCHAR(100) NOT NULL,
    action          VARCHAR(20)  NOT NULL,
    old_values      JSONB,
    new_values      JSONB,
    ip_address      INET,
    user_agent      TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_org ON tb_audit_log (organization_id, created_at);
CREATE INDEX idx_audit_entity ON tb_audit_log (entity_type, entity_id);

-- ============================================
-- AUTO-UPDATE TIMESTAMP FUNCTION
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_organizations_updated_at
    BEFORE UPDATE ON tb_organizations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON tb_users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();