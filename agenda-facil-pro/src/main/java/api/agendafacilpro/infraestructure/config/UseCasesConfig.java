package api.agendafacilpro.infraestructure.config;

import api.agendafacilpro.core.gateway.JwtTokenGateway;
import api.agendafacilpro.core.gateway.OrganizationGateway;
import api.agendafacilpro.core.gateway.PasswordEncoderGateway;
import api.agendafacilpro.core.gateway.UserGateway;
import api.agendafacilpro.core.usecases.auth.AuthenticateUserUseCase;
import api.agendafacilpro.core.usecases.organization.RegisterClinicUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {
    @Bean
    public RegisterClinicUseCase registerClinicUseCase(
            OrganizationGateway organizationGateway,
            UserGateway userGateway,
            PasswordEncoderGateway passwordEncoderGateway,
            JwtTokenGateway jwtTokenGateway
    ) {
        return new RegisterClinicUseCase(organizationGateway, userGateway, passwordEncoderGateway, jwtTokenGateway);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(UserGateway userGateway, PasswordEncoderGateway passwordEncoderGateway, JwtTokenGateway jwtTokenGateway) {
        return new AuthenticateUserUseCase(userGateway, passwordEncoderGateway, jwtTokenGateway);
    }
}
