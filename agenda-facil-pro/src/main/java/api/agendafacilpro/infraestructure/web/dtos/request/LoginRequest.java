package api.agendafacilpro.infraestructure.web.dtos.request;

import api.agendafacilpro.core.domain.valueobjects.Password;
import api.agendafacilpro.core.usecases.input.auth.LoginInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "Email cadastrado", example = "doutor@clinica.com")
        @NotBlank(message = "Email é obrigatório!")
        @Email(message = "Formato de email inválido")
        String userEmail,

        @Schema(description = "A senha de acesso", example = "SenhaForte123")
        @NotBlank(message = "A senha é obrigatório!")
        String userPassword
) {

    public LoginInput toUseCaseInput() {
        return new LoginInput(
                new api.agendafacilpro.core.domain.valueobjects.Email(userEmail),
                new Password(userPassword)
        );
    }
}
