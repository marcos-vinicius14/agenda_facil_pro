package api.agendafacilpro.infraestructure.persistence.entities;

import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.domain.valueobjects.Email;
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
@Table(name = "tb_users")
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "failed_attempts")
    private Integer failedAttempts = 0;

    @Column(name = "lockout_time")
    private Instant lockoutTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected UserJpaEntity() {}

    public UserJpaEntity(UUID id, UUID organizationId, String email, String passwordHash, Boolean enabled, Integer failedAttempts, Instant lockoutTime) {
        this.id = id;
        this.organizationId = organizationId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.failedAttempts = failedAttempts;
        this.lockoutTime = lockoutTime;
    }

    public User toDomain() {
        return User.builder()
                .withId(id)
                .withOrganizationId(organizationId)
                .withEmail(new Email(email))
                .withPasswordHash(passwordHash)
                .withEnabled(enabled)
                .withFailedAttempts(failedAttempts)
                .withLockoutTime(lockoutTime)
                .withCreatedAt(createdAt)
                .build();
    }

    public static UserJpaEntity fromDomain(User user) {
        UUID targetId = user.getId() != null ? user.getId() : UuidCreator.getTimeOrderedEpoch();

        return new UserJpaEntity(
                targetId,
                user.getOrganizationId(),
                user.getEmail().getValue(),
                user.getPasswordHash(),
                user.getEnabled(),
                user.getFailedAttempts(),
                user.getLockoutTime()
        );
    }

    public void updateFromDomain(User user) {
        this.organizationId = user.getOrganizationId();
        this.email = user.getEmail().getValue();
        this.passwordHash = user.getPasswordHash();
        this.enabled = user.getEnabled();
        this.failedAttempts = user.getFailedAttempts();
        this.lockoutTime = user.getLockoutTime();
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Boolean getEnabled() { return enabled; }
    public Integer getFailedAttempts() { return failedAttempts; }
    public Instant getLockoutTime() { return lockoutTime; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}