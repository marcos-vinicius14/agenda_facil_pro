package api.agendafacilpro.infraestructure.gateway;

import api.agendafacilpro.core.domain.entities.Patient;
import api.agendafacilpro.core.domain.shared.PaginatedResult;
import api.agendafacilpro.core.domain.shared.Pagination;
import api.agendafacilpro.core.exceptions.ValidationException;
import api.agendafacilpro.core.gateway.PatientGateway;
import api.agendafacilpro.infraestructure.persistence.entities.PatientJpaEntity;
import api.agendafacilpro.infraestructure.persistence.repository.PatientJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PatientRepositoryGateway implements PatientGateway {
    private final PatientJpaRepository repository;

    public PatientRepositoryGateway(PatientJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Patient create(Patient patient) {
        PatientJpaEntity entity = PatientJpaEntity.fromDomain(patient);
        PatientJpaEntity savedEntity = repository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    @CacheEvict(value = "patients", key = "#patient.id")
    public Patient update(Patient patient) {
        UUID id = patient.getId();

        if (id == null) {
            throw new ValidationException("O identificador do paciente deve ser fornecido para que ele possa ser atualizado.");
        }

        PatientJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado ou não existe"));

        entity.updateFromDomain(patient);

        return repository.save(entity).toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "patients", key = "#id")
    public Optional<Patient> findById(UUID id) {
        return repository.findById(id)
                .map(PatientJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<Patient> findAllByOrganizationId(UUID organizationId, Pagination pagination) {

        Pageable pageable = PageRequest.of(
                pagination.page(),
                pagination.size(),
                Sort.by("name").ascending()
        );

        Page<PatientJpaEntity> page = repository.findAllByOrganizationId(organizationId, pageable);

        List<Patient> domainPatients = page.stream()
                .map(PatientJpaEntity::toDomain)
                .toList();

        return new PaginatedResult<>(
                domainPatients,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDocument(UUID organizationId, String document) {
        return repository.existsByOrganizationIdAndDocument(organizationId, document);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(UUID organizationId, String email) {
        return repository.existsByOrganizationIdAndEmail(organizationId, email);
    }
}
