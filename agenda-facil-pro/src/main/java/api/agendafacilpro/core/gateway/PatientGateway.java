package api.agendafacilpro.core.gateway;

import api.agendafacilpro.core.domain.entities.Patient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientGateway {
    Patient create(Patient patient);
    Patient update(Patient patient);
    Optional<Patient> findById(Long id);
    List<Patient> findAllByOrganizationId(Long organizationId);
    boolean existsByDocument(UUID organizationId, String document);
    boolean existsByEmail(UUID organizationId,String email);
}
