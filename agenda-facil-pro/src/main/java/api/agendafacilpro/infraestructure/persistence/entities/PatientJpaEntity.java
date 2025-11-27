package api.agendafacilpro.infraestructure.persistence.entities;

import api.agendafacilpro.core.domain.entities.Patient;
import api.agendafacilpro.core.domain.valueobjects.CpfCnpj;
import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.core.domain.valueobjects.Phone;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_patients")
public class PatientJpaEntity {
    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true)
    private String document;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected PatientJpaEntity() {}

    public PatientJpaEntity(UUID id, UUID organizationId, String name, String email, String phone, String document, Boolean active, Instant createdAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.isActive = active;
        this.createdAt = createdAt;
    }

    public static PatientJpaEntity fromDomain(Patient patient) {
        UUID id = patient.getId() != null ? patient.getId() : UuidCreator.getTimeOrderedEpoch();

        return new PatientJpaEntity(
                id,
                patient.getOrganizationId(),
                patient.getName(),
                patient.getEmail() != null ? patient.getEmail().getValue() : null,
                patient.getPhone() != null ? patient.getPhone().getValue() : null,
                patient.getDocument() != null ? patient.getDocument().getValue() : null,
                patient.getIsActive(),
                patient.getCreatedAt()
        );
    }

    public Patient toDomain() {
        return Patient.builder()
                .withId(id)
                .withOrganizationId(organizationId)
                .withName(name)
                .withEmail(email != null ? new Email(email) : null)
                .withPhone(phone != null ? new Phone(phone) : null)
                .withDocument(document != null ? new CpfCnpj(document) : null)
                .withIsActive(isActive)
                .withCreatedAt(createdAt)
                .build();
    }

    // Método para updates eficientes (Dirty Checking)
    public void updateFromDomain(Patient patient) {
        this.name = patient.getName();
        this.email = patient.getEmail() != null ? patient.getEmail().getValue() : null;
        this.phone = patient.getPhone() != null ? patient.getPhone().getValue() : null;
        this.document = patient.getDocument() != null ? patient.getDocument().getValue() : null;
        this.isActive = patient.getIsActive();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDocument() { return document; }
    public Boolean getActive() { return isActive; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
