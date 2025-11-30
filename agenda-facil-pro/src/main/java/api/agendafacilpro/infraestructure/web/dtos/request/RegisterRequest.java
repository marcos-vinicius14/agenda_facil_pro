package api.agendafacilpro.infraestructure.web.dtos.request;

import api.agendafacilpro.core.usecases.input.clinic.RegisterClinicInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

public record RegisterRequest(
        @Schema(description = "Nome da Clínica", example = "Clínica Saúde Total")
        @NotBlank
        String organizationName,

        @Schema(description = "CNPJ (apenas números)", example = "12345678000199")
        @NotBlank
        @CNPJ
        String document,

        @Schema(description = "E-mail do administrador", example = "admin@saudetotal.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "Senha forte", example = "SenhaForte@123")
        @NotBlank
        @Size(min = 8)
        String password,

        @Schema(description = "Nome do administrador", example = "Dr. João Silva")
        @NotBlank
        String userName
) {
    public RegisterClinicInput toUseCaseInput() {
        return new RegisterClinicInput(
                organizationName, document, email, password, userName
        );
    }
}