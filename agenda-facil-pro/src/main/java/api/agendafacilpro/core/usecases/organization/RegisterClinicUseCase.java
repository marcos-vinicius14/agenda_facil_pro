package api.agendafacilpro.core.usecases.organization;

import api.agendafacilpro.core.domain.entities.Organization;
import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.domain.valueobjects.CpfCnpj;
import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.core.enums.PermissionType;
import api.agendafacilpro.core.exceptions.ValidationException;
import api.agendafacilpro.core.gateway.JwtTokenGateway;
import api.agendafacilpro.core.gateway.OrganizationGateway;
import api.agendafacilpro.core.gateway.PasswordEncoderGateway;
import api.agendafacilpro.core.gateway.UserGateway;
import api.agendafacilpro.core.usecases.UseCase;
import api.agendafacilpro.core.usecases.input.clinic.RegisterClinicInput;
import api.agendafacilpro.core.usecases.output.RegisterClinicOutPut;

import java.util.List;

public final class RegisterClinicUseCase implements UseCase<RegisterClinicInput, RegisterClinicOutPut> {

    private final OrganizationGateway organizationGateway;
    private final UserGateway userGateway;
    private final PasswordEncoderGateway passwordEncoderGateway; // Desacoplado do Spring
    private final JwtTokenGateway jwtTokenGateway;

    public RegisterClinicUseCase(OrganizationGateway organizationGateway,
                                 UserGateway userGateway,
                                 PasswordEncoderGateway passwordEncoderGateway,
                                 JwtTokenGateway jwtTokenGateway) {
        this.organizationGateway = organizationGateway;
        this.userGateway = userGateway;
        this.passwordEncoderGateway = passwordEncoderGateway;
        this.jwtTokenGateway = jwtTokenGateway;
    }

    @Override
    public RegisterClinicOutPut execute(RegisterClinicInput input) {
        validateOrganizationDoesNotExist(input.document());
        validateUserEmailDoesNotExist(input.email());

        Organization organization = createOrganization(input);
        User user = createUser(organization, input);

        return generateTokens(organization, user);
    }

    private void validateOrganizationDoesNotExist(String document) {
        if (organizationGateway.existsByDocument(document)) {
            throw new ValidationException("Já existe uma organização cadastrada com esse CNPJ.");
        }
    }

    private void validateUserEmailDoesNotExist(String emailRaw) {
        Email email = new Email(emailRaw);
        if (userGateway.existsByEmail(email.getValue())) {
            throw new ValidationException("Este e-mail já está em uso.");
        }
    }

    private Organization createOrganization(RegisterClinicInput input) {
        CpfCnpj document = new CpfCnpj(input.document());

        Organization organization = Organization.builder()
                .withName(input.organizationName())
                .withDocument(document)
                .build();

        return organizationGateway.create(organization);
    }

    private User createUser(Organization organization, RegisterClinicInput input) {
        Email email = new Email(input.email());

        String passwordHash = passwordEncoderGateway.encode(input.password());

        User user = User.builder()
                .withOrganizationId(organization.getId())
                .withEmail(email)
                .withPasswordHash(passwordHash)
                .withEnabled(true)
                .withFailedAttempts(0)
                .build();

        return userGateway.create(user);
    }

    private RegisterClinicOutPut generateTokens(Organization organization, User user) {
        List<String> permissions = List.of(
                PermissionType.ORG_READ.name(),
                PermissionType.ORG_UPDATE.name(),
                PermissionType.PATIENT_CREATE.name(),
                PermissionType.PATIENT_READ_ALL.name()
        );

        String accessToken = jwtTokenGateway.generateAccessToken(
                user.getId(),
                organization.getId(),
                user.getEmail().getValue(),
                permissions
        );

        String refreshToken = jwtTokenGateway.generateRefreshToken(user.getId());

        return new RegisterClinicOutPut(
                user.getId(),
                organization.getId(),
                user.getEmail().getValue(),
                accessToken,
                refreshToken
        );
    }
}