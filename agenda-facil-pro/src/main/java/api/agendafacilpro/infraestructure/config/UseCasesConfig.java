package api.agendafacilpro.infraestructure.config;

import api.agendafacilpro.core.gateway.*;
import api.agendafacilpro.core.usecases.auth.AuthenticateUserUseCase;
import api.agendafacilpro.core.usecases.organization.RegisterClinicUseCase;
import api.agendafacilpro.core.usecases.patient.CreatePatientUseCase;
import api.agendafacilpro.core.usecases.patient.ListPatientUseCase;
import api.agendafacilpro.core.usecases.patient.SoftDeletePatientUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {
    @Bean
    public CreatePatientUseCase createPatientUseCase(PatientGateway patientGateway) {
        return new CreatePatientUseCase(patientGateway);
    }

    @Bean
    public ListPatientUseCase  listPatientUseCase(PatientGateway patientGateway) {
        return new ListPatientUseCase(patientGateway);
    }

    @Bean
    public SoftDeletePatientUseCase softDeletePatientUseCase(PatientGateway patientGateway) {
        return new SoftDeletePatientUseCase(patientGateway);
    }

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
