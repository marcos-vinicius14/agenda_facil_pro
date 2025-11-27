package api.agendafacilpro.infraestructure.persistence;

import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.infraestructure.persistence.entities.OrganizationJpaEntity;
import api.agendafacilpro.infraestructure.persistence.entities.UserJpaEntity;
import api.agendafacilpro.infraestructure.persistence.repository.OrganizationJpaRepository;
import api.agendafacilpro.infraestructure.persistence.repository.UserJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private OrganizationJpaRepository organizationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should save user and retrieve permissions correctly using Native Query")
    void shouldSaveUserAndRetrievePermissions() {
        OrganizationJpaEntity org = new OrganizationJpaEntity(
                UUID.randomUUID(),
                "Clinica User Test",
                "99.999.999/0001-99",
                "BASIC"
        );
        organizationRepository.save(org);

        User domainUser = User.builder()
                .withOrganizationId(org.getId())
                .withEmail(new Email("admin@clinica.com"))
                .withPasswordHash("hash123")
                .build();

        UserJpaEntity userEntity = UserJpaEntity.fromDomain(domainUser);
        userRepository.saveAndFlush(userEntity);

        UUID adminRoleId = UUID.fromString("0193512b-ccf0-7000-8000-000000000001");

        entityManager.createNativeQuery("INSERT INTO tb_user_roles (user_id, role_id) VALUES (?1, ?2)")
                .setParameter(1, userEntity.getId())
                .setParameter(2, adminRoleId)
                .executeUpdate();

        List<String> permissions = userRepository.findPermissionsByUserId(userEntity.getId());

        assertThat(permissions).isNotEmpty();
        assertThat(permissions).contains("ORG_READ", "ORG_UPDATE", "PATIENT_CREATE");
    }
}