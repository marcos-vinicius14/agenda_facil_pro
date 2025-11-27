
CREATE TABLE tb_patients
(
    id              UUID PRIMARY KEY,
    organization_id UUID         NOT NULL REFERENCES tb_organizations (id) ON DELETE CASCADE,

    name            VARCHAR(255) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(20),
    document        VARCHAR(20),

    active          BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);



CREATE INDEX idx_patients_org ON tb_patients (organization_id);

CREATE UNIQUE INDEX idx_patients_org_document
    ON tb_patients (organization_id, document) WHERE document IS NOT NULL;

CREATE UNIQUE INDEX idx_patients_org_email
    ON tb_patients (organization_id, email) WHERE email IS NOT NULL;

CREATE INDEX idx_patients_org_name ON tb_patients (organization_id, name);


CREATE TRIGGER update_patients_updated_at
    BEFORE UPDATE
    ON tb_patients
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
