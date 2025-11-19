package api.agendafacilpro.core.gateway;

import api.agendafacilpro.core.domain.entities.Organization;

import java.util.Optional;

public interface OrganizationGateway {
    Organization save(Organization organization);
    Optional<Organization> findById(Long id);
    boolean existsByDocument(String document);
}
