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
    organization_id UUID       NOT NULL REFERENCES tb_organizations (id) ON DELETE CASCADE,
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
    user_id    UUID       NOT NULL UNIQUE REFERENCES tb_users (id) ON DELETE CASCADE,
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

INSERT INTO tb_roles (name, description)
VALUES ('ADMIN', 'System Administrator with full access'),
       ('DENTIST', 'Dentist owner of clinic with management permissions'),
       ('SECRETARY', 'Secretary with operational permissions');

-- ============================================
-- PERMISSIONS (GRANULAR)
-- ============================================
CREATE TABLE tb_permissions
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

INSERT INTO tb_permissions (name, description)
VALUES ('ORG_READ', 'Read organization details'),
       ('ORG_UPDATE', 'Update organization settings'),
       ('PATIENT_CREATE', 'Create new patients'),
       ('PATIENT_READ_ALL', 'Read all patients from organization'),
       ('APPOINTMENT_CREATE', 'Create appointments'),
       ('APPOINTMENT_RESCHEDULE_OWN', 'Reschedule own appointments only'),
       ('APPOINTMENT_RESCHEDULE_ANY', 'Reschedule any appointment'),
       ('APPOINTMENT_CANCEL_OWN', 'Cancel own appointments only'),
       ('APPOINTMENT_CANCEL_ANY', 'Cancel any appointment'),
       ('FINANCIAL_VIEW', 'View financial reports (Dentist only)');

-- ============================================
-- ROLE_PERMISSIONS (MANY-TO-MANY)
-- ============================================
CREATE TABLE tb_role_permissions
(
    role_id       UUID NOT NULL REFERENCES tb_roles (id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES tb_permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- DENTIST gets all permissions
INSERT INTO tb_role_permissions (role_id, permission_id)
SELECT 2, id
FROM tb_permissions;

-- SECRETARY gets operational permissions only
INSERT INTO tb_role_permissions (role_id, permission_id)
VALUES (3, 3),
       (3, 4),
       (3, 5),
       (3, 7),
       (3, 9);
-- CREATE_PATIENT, READ_PATIENT, CREATE_APPT, RESCHEDULE_ANY, CANCEL_ANY

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
    organization_id UUID       NOT NULL REFERENCES tb_organizations (id),
    user_id         UUID       NOT NULL REFERENCES tb_users (id),
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
CREATE
OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at
= CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER update_organizations_updated_at
    BEFORE UPDATE
    ON tb_organizations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE
    ON tb_users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();