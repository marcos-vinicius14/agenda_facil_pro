package api.agendafacilpro.infraestructure.gateway;

import api.agendafacilpro.core.domain.entities.Organization;
import api.agendafacilpro.core.exceptions.OrganizationNotFoundException;
import api.agendafacilpro.core.gateway.OrganizationGateway;
import api.agendafacilpro.infraestructure.persistence.entities.OrganizationJpaEntity;
import api.agendafacilpro.infraestructure.persistence.repository.OrganizationJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class OrganizationRepositoryGateway implements OrganizationGateway {

    private final OrganizationJpaRepository repository;

    public OrganizationRepositoryGateway(OrganizationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Organization create(Organization organization) {
        OrganizationJpaEntity entity = OrganizationJpaEntity.fromDomain(organization);
        OrganizationJpaEntity savedEntity = repository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    @Transactional
    public Organization update(Organization organization) {
        UUID id = organization.getId();

        if (id == null) {
            throw new IllegalArgumentException("Organização não pode ser atualizada sem ID.");
        }

        OrganizationJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organização não encontrada para atualização: " + id));

        entity.updateFromDomain(organization);

        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<Organization> findById(UUID id) {
        return repository.findById(id)
                .map(OrganizationJpaEntity::toDomain);
    }

    @Override
    public boolean existsByDocument(String document) {
        return repository.existsByDocument(document);
    }
}