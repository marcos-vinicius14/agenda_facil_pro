package api.agendafacilpro.core.domain.entities;


import api.agendafacilpro.core.domain.valueobjects.CpfCnpj;
import api.agendafacilpro.core.exceptions.ValidationException;

public final class Organization {
    private final Long id;
    private final Info info;

    private Organization(Builder builder) {
        this.id = builder.id;
        this.info = builder.build().info;
        validate();
    }

    private void validate() {
        if (info.name() == null || info.name().isBlank()) {
            throw new ValidationException("Sua organização deve ter um nome");
        }
        if (info.document() == null) {
            throw new ValidationException("O CNPJ da organização é obrigatório.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return info.name();
    }

    public CpfCnpj getDocument() {
        return info.document();
    }

    public String getSubscriptionTier() {
        return info.subscriptionTier();
    }

    private record Info(String name, CpfCnpj document, String subscriptionTier) {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private CpfCnpj document;
        private String subscriptionTier = "BASIC";

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDocument(CpfCnpj document) {
            this.document = document;
            return this;
        }

        public Builder withSubscriptionTier(String tier) {
            this.subscriptionTier = tier;
            return this;
        }

        public Organization build() {
            return new Organization(this);
        }
    }
}