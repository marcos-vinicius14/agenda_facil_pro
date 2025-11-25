package api.agendafacilpro.core.domain.entities;

import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.core.exceptions.ValidationException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public final class User {

    private final UUID id;
    private final UUID organizationId;
    private final Email email;
    private final String passwordHash;
    private final Instant createdAt;

    private Boolean enabled;
    private Integer failedAttempts;
    private Instant lockoutTime;

    private User(Builder builder) {
        this.id = builder.id;
        this.organizationId = builder.organizationId;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.enabled = builder.enabled;
        this.failedAttempts = builder.failedAttempts;
        this.lockoutTime = builder.lockoutTime;

        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();

        validate();
    }

    private void validate() {
        if (organizationId == null) throw new ValidationException("O ID da organização deve ser informado");
        if (email == null) throw new ValidationException("O email é obrigatório.");
        if (passwordHash == null || passwordHash.isBlank()) throw new ValidationException("A senha é obrigatória");
    }

    public void recordFailedLogin() {
        this.failedAttempts++;

        if (this.failedAttempts >= 5) {
            this.enabled = false;
            this.lockoutTime = Instant.now();
        }
    }

    public boolean isLockExpired() {
        if (this.lockoutTime == null) return true;

        long LOCK_TIME_MINUTES = 30;
        Instant unlockTime = this.lockoutTime.plus(LOCK_TIME_MINUTES, ChronoUnit.MINUTES);
        return Instant.now().isAfter(unlockTime);
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.enabled = true;
        this.lockoutTime = null;
    }

    public boolean isLocked() {
        return Boolean.FALSE.equals(this.enabled);
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Boolean getEnabled() { return enabled; }
    public Integer getFailedAttempts() { return failedAttempts; }
    public Instant getLockoutTime() { return lockoutTime; }
    public Instant getCreatedAt() { return createdAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID organizationId;
        private Email email;
        private String passwordHash;
        private Boolean enabled = true;
        private Integer failedAttempts = 0;
        private Instant lockoutTime;
        private Instant createdAt;

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withOrganizationId(UUID organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder withEmail(Email email) {
            this.email = email;
            return this;
        }

        public Builder withPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder withEnabled(Boolean enabled) {
            this.enabled = enabled != null ? enabled : true;
            return this;
        }

        public Builder withFailedAttempts(Integer failedAttempts) {
            this.failedAttempts = failedAttempts != null ? failedAttempts : 0;
            return this;
        }

        public Builder withLockoutTime(Instant lockoutTime) {
            this.lockoutTime = lockoutTime;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}