package api.agendafacilpro.infraestructure.web.dtos.request;

import api.agendafacilpro.core.usecases.input.patient.CreatePatientInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePatientRequest(
        @Schema(description = "Nome completo do paciente", example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @Schema(description = "E-mail para contato e notificações", example = "maria.silva@email.com")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @Schema(description = "Telefone com DDD", example = "11999998888")
        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(regexp = "^\\d{10,11}$", message = "O telefone deve conter apenas números (DDD + Número)")
        String phone,

        @Schema(description = "CPF (apenas números)", example = "12345678900")
        @NotBlank(message = "O documento é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "O CPF deve conter 11 dígitos numéricos")
        String document
) {
    public CreatePatientInput toInput() {
        return new CreatePatientInput(name, email, phone, document);
    }
}