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
import api.agendafacilpro.infraestructure.web.dtos.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatientIntegrationTest extends BaseIntegrationTest {
    private static final String VALID_CPF = "93644112300";
    private static final String VALID_CPF_2 = "12345678909";

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

        cookieUserA = doLogin("admin.a@clinica.com", "123456789");
    }

    @Test
    @DisplayName("Should create patient successfully when authenticated")
    void shouldCreatePatient() throws Exception {
        CreatePatientRequest request = new CreatePatientRequest(
                "João da Silva",
                "joao@gmail.com",
                "11999998888",
                generateValidCpf()
        );

        mockMvc.perform(post("/api/v1/patients")
                        .cookie(cookieUserA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("João da Silva")))
                .andExpect(jsonPath("$.isActive", is(true)));

        assertThat(patientRepo.findAll()).hasSize(1);
        PatientJpaEntity saved = patientRepo.findAll().getFirst();
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
        Cookie cookieUserB = doLogin("admin.b@clinica.com", "123456789");

        createPatientViaApi(cookieUserA, "Paciente A", generateValidCpf());

        createPatientViaApi(cookieUserB, "Paciente B", generateValidCpf());

        mockMvc.perform(get("/api/v1/patients")
                        .cookie(cookieUserA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("Paciente A")));

        mockMvc.perform(get("/api/v1/patients")
                        .cookie(cookieUserB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("Paciente B")));
    }

    @Test
    @DisplayName("Should soft delete patient")
    void shouldSoftDeletePatient() throws Exception {
        createPatientViaApi(cookieUserA, "Maria Delete", generateValidCpf());

        UUID patientId = patientRepo
                .findAll()
                .getFirst()
                .getId();

        mockMvc.perform(delete("/api/v1/patients/" + patientId)
                        .cookie(cookieUserA))
                .andExpect(status().isNoContent());

        PatientJpaEntity patient = patientRepo.findById(patientId).orElseThrow();
        assertThat(patient.getActive()).isFalse();
    }



    private UserJpaEntity createUser(OrganizationJpaEntity org, String email) {
        User userDomain = User.builder()
                .withOrganizationId(org.getId())
                .withEmail(new Email(email))
                .withPasswordHash(encoder.encode("123456789"))
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
        var loginRequest = new LoginRequest(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(status().isOk())
                        .andReturn();

        return result.getResponse().getCookie("access_token");
    }

    private void createPatientViaApi(Cookie cookie, String name, String doc) throws Exception {
        CreatePatientRequest request = new CreatePatientRequest(name, "email@teste.com", "11999999999", doc);
        mockMvc.perform(post("/api/v1/patients")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private String generateValidCpf() {
        Random random = new Random();
        int n1 = random.nextInt(10);
        int n2 = random.nextInt(10);
        int n3 = random.nextInt(10);
        int n4 = random.nextInt(10);
        int n5 = random.nextInt(10);
        int n6 = random.nextInt(10);
        int n7 = random.nextInt(10);
        int n8 = random.nextInt(10);
        int n9 = random.nextInt(10);
        int d1 = n9 * 2 + n8 * 3 + n7 * 4 + n6 * 5 + n5 * 6 + n4 * 7 + n3 * 8 + n2 * 9 + n1 * 10;
        d1 = 11 - (d1 % 11);
        if (d1 > 9) d1 = 0;
        int d2 = d1 * 2 + n9 * 3 + n8 * 4 + n7 * 5 + n6 * 6 + n5 * 7 + n4 * 8 + n3 * 9 + n2 * 10 + n1 * 11;
        d2 = 11 - (d2 % 11);
        if (d2 > 9) d2 = 0;

        return String.format("%d%d%d%d%d%d%d%d%d%d%d", n1, n2, n3, n4, n5, n6, n7, n8, n9, d1, d2);
    }
}