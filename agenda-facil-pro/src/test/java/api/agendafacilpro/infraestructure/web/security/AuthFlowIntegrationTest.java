package api.agendafacilpro.infraestructure.web.security;

import api.agendafacilpro.BaseIntegrationTest;
import api.agendafacilpro.core.gateway.PasswordEncoderGateway;
import api.agendafacilpro.infraestructure.persistence.repository.OrganizationJpaRepository;
import api.agendafacilpro.infraestructure.persistence.repository.UserJpaRepository;
import api.agendafacilpro.infraestructure.web.dtos.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class AuthFlowIntegrationTest extends BaseIntegrationTest {


    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrganizationJpaRepository orgRepo;
    @Autowired private UserJpaRepository userRepo;
    @Autowired private PasswordEncoderGateway encoder;

    @Test
    @DisplayName("Full Flow: Register -> Cookie Receive -> Protected Route Access")
    void shouldAuthenticateViaCookies() throws Exception {

        String uniqueEmail = "doutor." + UUID.randomUUID() + "@teste.com";

        mockMvc.perform(get("/api/debug/thread"))
                .andExpect(status().isForbidden());

        RegisterRequest registerRequest = new RegisterRequest(
                "Clínica Teste",
                "33.592.510/0001-54",
                uniqueEmail,
                "senhaForte123",
                "Dr. Teste"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().httpOnly("access_token", true))
                .andReturn();

        Cookie accessTokenCookie = result.getResponse().getCookie("access_token");
        assertNotNull(accessTokenCookie);

        mockMvc.perform(get("/api/debug/thread")
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Thread")));
    }
}
