package api.agendafacilpro.infraestructure.persistence.entities;

import api.agendafacilpro.core.domain.entities.Organization;
import api.agendafacilpro.core.domain.valueobjects.CpfCnpj;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_organizations")
public class OrganizationJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String document;

    @Column(name = "subscription_tier")
    private String subscriptionTier;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    protected OrganizationJpaEntity() {}

    public OrganizationJpaEntity(UUID id, String name, String document, String subscriptionTier) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.subscriptionTier = subscriptionTier;
    }

    public Organization toDomain() {
        return Organization.builder()
                .withId(id)
                .withName(name)
                .withDocument(new CpfCnpj(document))
                .withSubscriptionTier(subscriptionTier)
                .build();
    }

    public static OrganizationJpaEntity fromDomain(Organization org) {
        UUID targetId = org.getId() != null ? org.getId() : UuidCreator.getTimeOrderedEpoch();

        return new OrganizationJpaEntity(
                targetId,
                org.getName(),
                org.getDocument().getValue(),
                org.getSubscriptionTier()
        );
    }

    public void updateFromDomain(Organization org) {
        this.name = org.getName();
        this.document = org.getDocument().getValue();
        this.subscriptionTier = org.getSubscriptionTier();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getSubscriptionTier() { return subscriptionTier; }
    public Instant getCreatedAt() { return createdAt; }
}