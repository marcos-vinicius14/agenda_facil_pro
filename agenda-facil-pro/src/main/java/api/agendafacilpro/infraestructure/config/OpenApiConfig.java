package api.agendafacilpro.infraestructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agenda Fácil Pro API")
                        .description("API para gestão de clínicas e agendamentos.")
                        .contact(new Contact().name("Seu Nome").email("seuemail@dominio.com"))
                        .version("1.0.0"));
    }
}