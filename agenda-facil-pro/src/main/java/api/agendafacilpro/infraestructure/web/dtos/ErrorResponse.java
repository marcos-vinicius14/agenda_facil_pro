package api.agendafacilpro.infraestructure.web.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        @Schema(description = "Timestamp do erro", example = "2025-11-21T10:00:00Z")
        Instant timestamp,

        @Schema(description = "Código HTTP", example = "400")
        Integer status,

        @Schema(description = "Tipo de erro", example = "Bad Request")
        String error,

        @Schema(description = "Mensagem do erro", example = "Erro interno do servidor")
        String message,

        @Schema(description = "URL do erro", example = "/api/v1/clinics")
        String path,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<ValidationError> errors
) {
    public static ErrorResponse simple(Integer status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, null);
    }

    public static ErrorResponse withValidation(Integer status, String error, String message, String path, List<ValidationError> errors) {
        return new ErrorResponse(Instant.now(), status, error, message, path, errors);
    }
}

