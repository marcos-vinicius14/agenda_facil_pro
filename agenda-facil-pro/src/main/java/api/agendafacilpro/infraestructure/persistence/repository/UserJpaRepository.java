package api.agendafacilpro.infraestructure.persistence.repository;

import api.agendafacilpro.infraestructure.persistence.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = """
            SELECT DISTINCT p.name
            FROM tb_permissions p
            INNER JOIN tb_role_permissions rp ON p.id = rp.permission_id
            INNER JOIN tb_user_roles ur ON rp.role_id = ur.role_id
            WHERE ur.user_id = :userId
            """, nativeQuery = true)
    List<String> findPermissionsByUserId(@Param("userId") UUID userId);
}
