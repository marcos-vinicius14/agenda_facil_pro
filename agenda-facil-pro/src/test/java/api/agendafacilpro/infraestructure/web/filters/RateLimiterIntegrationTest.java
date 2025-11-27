package api.agendafacilpro.infraestructure.web.filters;

import api.agendafacilpro.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RateLimiterIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return 429 Too Many Requests when limit is exceeded")
    public void shouldBlockRequestsWhenLimitExceeded() throws Exception {
        String uniqueIp = "192.168.0.55";
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            mockMvc.perform(post("/api/auth/login")
                            .header("X-Forwarded-For", uniqueIp)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"teste@teste.com\", \"password\":\"123\"}"))
                    .andExpect(result -> {
                        if (result.getResponse().getStatus() == 429) {
                            throw new AssertionError("Bloqueou antes da hora! Req: " + finalI);
                        }
                    });
        }

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", uniqueIp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"teste@teste.com\", \"password\":\"123\"}"))
                .andExpect(status().isTooManyRequests());
    }
}
