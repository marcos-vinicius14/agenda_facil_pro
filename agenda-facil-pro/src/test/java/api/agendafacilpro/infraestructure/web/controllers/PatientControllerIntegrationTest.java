package api.agendafacilpro.infraestructure.web.controllers;

import api.agendafacilpro.BaseIntegrationTest;
import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.core.gateway.PasswordEncoderGateway;
import api.agendafacilpro.infraestructure.persistence.entities.OrganizationJpaEntity;
import api.agendafacilpro.infraestructure.persistence.entities.PatientJpaEntity;
import api.agendafacilpro.infraestructure.persistence.entities.UserJpaEntity;
import api.agendafacilpro.infraestructure.persistence.repository.OrganizationJpaRepository;
import api.agendafacilpro.infraestructure.persistence.repository.PatientJpaRepository;
import api.agendafacilpro.infraestructure.persistence.repository.UserJpaRepository;
import api.agendafacilpro.infraestructure.web.dtos.request.CreatePatientRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatientIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrganizationJpaRepository orgRepo;
    @Autowired private UserJpaRepository userRepo;
    @Autowired private PatientJpaRepository patientRepo;
    @Autowired private PasswordEncoderGateway encoder;
    @Autowired private EntityManager entityManager;

    private OrganizationJpaEntity orgA;
    private UserJpaEntity userA;
    private Cookie cookieUserA;

    @BeforeEach
    void setup() throws Exception {
        patientRepo.deleteAll();
        userRepo.deleteAll();
        orgRepo.deleteAll();

        orgA = new OrganizationJpaEntity(
                UUID.randomUUID(),
                "Clinica A",
                "12.345.678/0001-90",
                "PREMIUM"
                );
        orgRepo.save(orgA);

        userA = createUser(orgA, "admin.a@clinica.com");

        cookieUserA = doLogin("admin.a@clinica.com", "123456");
    }

    @Test
    @DisplayName("Should create patient successfully when authenticated")
    void shouldCreatePatient() throws Exception {
        CreatePatientRequest request = new CreatePatientRequest(
                "João da Silva",
                "joao@gmail.com",
                "11999998888",
                "96632439066"
        );

        mockMvc.perform(post("/api/patients")
                        .cookie(cookieUserA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("João da Silva")))
                .andExpect(jsonPath("$.active", is(true)));

        assertThat(patientRepo.findAll()).hasSize(1);
        PatientJpaEntity saved = patientRepo.findAll().get(0);
        assertThat(saved.getOrganizationId()).isEqualTo(orgA.getId());
    }

    @Test
    @DisplayName("Tenant Isolation: User A should NOT see patients from Org B")
    void shouldRespectTenantIsolation() throws Exception {
        OrganizationJpaEntity orgB = new OrganizationJpaEntity(
                UUID.randomUUID(),
                "Clinica B",
                "98.765.432/0001-00",
                "BASIC"
        );
        orgRepo.save(orgB);
        UserJpaEntity userB = createUser(orgB, "admin.b@clinica.com");
        Cookie cookieUserB = doLogin("admin.b@clinica.com", "123456");

        createPatientViaApi(cookieUserA, "Paciente A", "11111111111");

        createPatientViaApi(cookieUserB, "Paciente B", "22222222222");

        mockMvc.perform(get("/api/patients")
                        .cookie(cookieUserA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("Paciente A")));

        mockMvc.perform(get("/api/patients")
                        .cookie(cookieUserB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("Paciente B")));
    }

    @Test
    @DisplayName("Should soft delete patient")
    void shouldSoftDeletePatient() throws Exception {
        String cpf = "96632439066";
        createPatientViaApi(cookieUserA, "Maria Delete", cpf);

        UUID patientId = patientRepo
                .findAll()
                .getFirst()
                .getId();

        mockMvc.perform(delete("/api/patients/" + patientId)
                        .cookie(cookieUserA))
                .andExpect(status().isNoContent());

        PatientJpaEntity patient = patientRepo.findById(patientId).orElseThrow();
        assertThat(patient.getActive()).isFalse();
    }



    private UserJpaEntity createUser(OrganizationJpaEntity org, String email) {
        User userDomain = User.builder()
                .withOrganizationId(org.getId())
                .withEmail(new Email(email))
                .withPasswordHash(encoder.encode("123456"))
                .build();

        UserJpaEntity entity = userRepo.save(UserJpaEntity.fromDomain(userDomain));

        addAdminRole(entity.getId());

        return entity;
    }

    @Transactional
    protected void addAdminRole(UUID userId) {
        UUID adminRoleId = UUID.fromString("0193512b-ccf0-7000-8000-000000000001");
        entityManager.createNativeQuery("INSERT INTO tb_user_roles (user_id, role_id) VALUES (?1, ?2)")
                .setParameter(1, userId)
                .setParameter(2, adminRoleId)
                .executeUpdate();
    }

    private Cookie doLogin(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getCookie("access_token");
    }

    private void createPatientViaApi(Cookie cookie, String name, String doc) throws Exception {
        CreatePatientRequest request = new CreatePatientRequest(name, "email@teste.com", "11999999999", doc);
        mockMvc.perform(post("/api/patients")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}