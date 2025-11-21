package api.agendafacilpro.infraestructure.persistence;

import api.agendafacilpro.core.domain.entities.Organization;
import api.agendafacilpro.core.domain.valueobjects.CpfCnpj;
import api.agendafacilpro.infraestructure.persistence.entities.OrganizationJpaEntity;
import api.agendafacilpro.infraestructure.persistence.repository.OrganizationJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@EnableJpaRepositories(basePackages = "api.agendafacilpro.infraestructure.persistence.repository")
@EntityScan(basePackages = "api.agendafacilpro.infraestructure.persistence.entities")
class OrganizationRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private OrganizationJpaRepository repository;

    @Test
    @DisplayName("Should save and retrieve organization using UUID v7")
    void shouldSaveAndRetrieveOrganization() {
        String validCnpj = "06.990.590/0001-23";
        Organization domainOrg = Organization.builder()
                .withName("Clinica Teste")
                .withDocument(new CpfCnpj(validCnpj))
                .withSubscriptionTier("PREMIUM")
                .build();

        OrganizationJpaEntity entity = OrganizationJpaEntity.fromDomain(domainOrg);
        OrganizationJpaEntity saved = repository.save(entity);
        OrganizationJpaEntity found = repository.findById(saved.getId()).orElseThrow();

        assertThat(found).isNotNull();
        assertThat(found.getId()).isNotNull();
        assertThat(found.getName()).isEqualTo("Clinica Teste");
        assertThat(found.getDocument()).isEqualTo("06990590000123");
    }

    @Test
    @DisplayName("Should fail when trying to save duplicate document (CNPJ)")
    void shouldPreventDuplicateDocument() {
        String anotherValidCnpj = "60.316.817/0001-03";
        OrganizationJpaEntity org1 = createEntity("Clinica 1", anotherValidCnpj);
        repository.save(org1);

        OrganizationJpaEntity org2 = createEntity("Clinica 2", anotherValidCnpj);

        assertThatThrownBy(() -> repository.saveAndFlush(org2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private OrganizationJpaEntity createEntity(String name, String doc) {
        return OrganizationJpaEntity.fromDomain(
                Organization.builder()
                        .withName(name)
                        .withDocument(new CpfCnpj(doc))
                        .build()
        );
    }
}