package api.agendafacilpro.infraestructure.persistence.repository;

import api.agendafacilpro.infraestructure.persistence.entities.OrganizationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationJpaRepository extends JpaRepository<OrganizationJpaEntity, UUID> {
    boolean existsByDocument(String document);
}
