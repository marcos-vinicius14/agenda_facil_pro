package api.agendafacilpro.core.gateway;

import api.agendafacilpro.core.domain.entities.Patient;
import api.agendafacilpro.core.domain.shared.PaginatedResult;
import api.agendafacilpro.core.domain.shared.Pagination;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientGateway {
    Patient create(Patient patient);
    Patient update(Patient patient);
    Optional<Patient> findById(UUID id);
    PaginatedResult<Patient> findAllByOrganizationId(UUID organizationId, Pagination pagination);
    boolean existsByDocument(UUID organizationId, String document);
    boolean existsByEmail(UUID organizationId,String email);
}
