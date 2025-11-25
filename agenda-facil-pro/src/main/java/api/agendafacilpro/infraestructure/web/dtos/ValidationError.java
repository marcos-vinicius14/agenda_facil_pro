package api.agendafacilpro.infraestructure.web.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record ValidationError(
        @Schema(description = "Nome do campo com erro", example = "email")
        String field,

        @Schema(description = "Mensagem de erro", example = "Deve ser um e-mail válido")
        String message
) {}