package api.agendafacilpro.infraestructure.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PatientResponse(
        UUID id,
        String name,
        String email,

        @Schema(description = "Indica se o paciente esta ativo no sistema.")
        Boolean isActive

) {
}
