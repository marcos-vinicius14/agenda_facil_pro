package api.agendafacilpro.core.usecases.auth;

import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.core.exceptions.ValidationException;
import api.agendafacilpro.core.gateway.JwtTokenGateway;
import api.agendafacilpro.core.gateway.PasswordEncoderGateway;
import api.agendafacilpro.core.gateway.UserGateway;
import api.agendafacilpro.core.usecases.UseCase;
import api.agendafacilpro.core.usecases.input.LoginInput;
import api.agendafacilpro.core.usecases.output.LoginOutput;

import java.util.List;

public final class AuthenticateUserUseCase implements UseCase<LoginInput, LoginOutput> {

    private final UserGateway userGateway;
    private final PasswordEncoderGateway passwordEncoder;
    private final JwtTokenGateway jwtTokenGateway;

    public AuthenticateUserUseCase(
            UserGateway userGateway,
            PasswordEncoderGateway passwordEncoder,
            JwtTokenGateway jwtTokenGateway) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenGateway = jwtTokenGateway;
    }

    @Override
    public LoginOutput execute(LoginInput input) {
        Email email = new Email(input.email().getValue());

        User user = userGateway.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Usuário ou senha inválidos"));

        checkLockStatus(user);

        try {
            validatePassword(input.password(), user);
        } catch (ValidationException e) {
            user.recordFailedLogin();
            userGateway.update(user);
            throw e;
        }

        if (user.getFailedAttempts() > 0 || user.getLockoutTime() != null) {
            user.resetFailedAttempts();
            userGateway.update(user);
        }

        List<String> permissions = userGateway.findPermissionsUserId(user.getId());

        String accessToken = jwtTokenGateway.generateAccessToken(
                user.getId(),
                user.getOrganizationId(),
                user.getEmail().getValue(),
                permissions
        );

        String refreshToken = jwtTokenGateway.generateRefreshToken(user.getId());


        return new LoginOutput(
                user.getId(),
                user.getOrganizationId(),
                user.getEmail().getValue(),
                accessToken,
                refreshToken
        );
    }

    private void checkLockStatus(User user) {
        if (user.isLocked()) {
            if (user.isLockExpired()) {
                user.resetFailedAttempts();
            } else {
                throw new ValidationException("Conta bloqueada temporariamente devido a muitas tentativas falhas. Tente novamente mais tarde.");
            }
        }
    }

    private void validatePassword(String rawPassword, User user) {
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new ValidationException("Usuário ou senha inválidos");
        }
    }
}