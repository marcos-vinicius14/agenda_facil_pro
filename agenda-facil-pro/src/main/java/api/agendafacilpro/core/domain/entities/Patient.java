package api.agendafacilpro.core.domain.entities;

import api.agendafacilpro.core.domain.valueobjects.CpfCnpj;
import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.core.domain.valueobjects.Phone;
import api.agendafacilpro.core.exceptions.ValidationException;

import java.time.Instant;
import java.util.UUID;



/**
 * Representa um Paciente dentro do ecossistema da clínica.
 * <p>
 * Esta entidade segue o princípio de <b>Imutabilidade</b>. Uma vez criada,
 * seus atributos não podem ser alterados diretamente. Qualquer operação de
 * mudança de estado (como desativação ou atualização de cadastro) resultará
 * em uma nova instância da classe.
 * </p>
 *
 * <h3>Regras de Negócio e Invariantes:</h3>
 * <ul>
 * <li>Todo paciente deve pertencer a uma Organização (Tenant).</li>
 * <li>O nome é obrigatório.</li>
 * <li>A exclusão é lógica (soft delete) através do campo {@code active}.</li>
 * </ul>
 *
 * @author Marcos Vinicius
 * @version 1.0
 */
public final class Patient {
    private final UUID id;
    private final UUID organizationId;

    private final Info info;
    private final boolean isActive;
    private final Instant createdAt;

    private Patient(Builder builder) {
        this.id = builder.id;
        this.organizationId = builder.organizationId;
        this.isActive = builder.isActive;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();

        this.info = new Info(
                builder.name,
                builder.phone,
                builder.email,
                builder.document
        );

        validate();
    }

    /**
     * Realiza a exclusão lógica (Soft Delete) do paciente.
     * <p>
     * Como a entidade é imutável, este método não altera a instância atual.
     * Em vez disso, ele cria e retorna uma <b>cópia modificada</b> com
     * {@code active = false}.
     * </p>
     *
     * @return Uma nova instância de {@link Patient} com o estado desativado.
     */
    public Patient deactivate() {
        return new Builder()
                .withId(this.id)
                .withOrganizationId(this.organizationId)
                .withName(this.info.name())
                .withPhone(this.info.phone())
                .withEmail(this.info.email())
                .withDocument(this.info.document())
                .withCreatedAt(this.createdAt)
                .withIsActive(false)
                .build();
    }

    private void validate() {
        if (organizationId == null) {
            throw new ValidationException("O ID da organização é obrigatório.");
        }
        if (info.name() == null || info.name().isBlank()) {
            throw new ValidationException("O nome do paciente é obrigatório.");
        }

        if (info.document() == null) {
            throw new ValidationException("O documento de identificação não pode ser nulo.");
        }

        if (info.email.getValue().isBlank() && info.phone.getValue().isBlank()) {
            throw new ValidationException("O paciente deve ter pelo menos um contato. Sendo email ou telefone.");
        }
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getName() { return info.name(); }
    public Phone getPhone() { return info.phone(); }
    public Email getEmail() { return info.email(); }
    public CpfCnpj getDocument() { return info.document(); }
    public Boolean getIsActive() { return isActive; }
    public Instant getCreatedAt() { return createdAt; }

    private record Info(String name, Phone phone, Email email, CpfCnpj document) {}

    public static class Builder {
        private UUID id;
        private UUID organizationId;
        private String name;
        private Phone phone;
        private Email email;
        private CpfCnpj document;
        private Boolean isActive = true;
        private Instant createdAt;

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withOrganizationId(UUID organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPhone(Phone phone) {
            this.phone = phone;
            return this;
        }

        public Builder withEmail(Email email) {
            this.email = email;
            return this;
        }

        public Builder withDocument(CpfCnpj document) {
            this.document = document;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withIsActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Patient build() {
            return new Patient(this);
        }

    }
}
