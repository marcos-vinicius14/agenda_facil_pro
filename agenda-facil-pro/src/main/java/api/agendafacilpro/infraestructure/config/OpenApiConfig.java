package api.agendafacilpro.infraestructure.config;

import api.agendafacilpro.core.domain.valueobjects.CpfCnpj;
import api.agendafacilpro.core.domain.valueobjects.Email;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().replaceWithClass(CpfCnpj.class, String.class);
        SpringDocUtils.getConfig().replaceWithClass(Email.class, String.class);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agenda Fácil Pro API")
                        .description("API de gestão para clínicas multiserviço.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Time de Desenvolvimento")
                                .email("dev@agendafacil.com.br")));
    }
}