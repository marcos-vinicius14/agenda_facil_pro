package api.agendafacilpro.core.gateway;

import api.agendafacilpro.core.domain.entities.Organization;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationGateway {
    Organization create(Organization organization);
    Organization update(Organization organization);
    Optional<Organization> findById(UUID id);
    boolean existsByDocument(String document);
}
