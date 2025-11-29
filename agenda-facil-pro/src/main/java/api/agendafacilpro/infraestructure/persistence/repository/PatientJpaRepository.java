package api.agendafacilpro.infraestructure.persistence.repository;

import api.agendafacilpro.infraestructure.persistence.entities.PatientJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatientJpaRepository extends JpaRepository<PatientJpaEntity, UUID> {
    boolean existsByOrganizationIdAndEmail(UUID organizationId, String email);
    boolean existsByOrganizationIdAndDocument(UUID organizationId, String document);
    Page<PatientJpaEntity> findAllByOrganizationId(UUID organizationId, Pageable pageable);
}
