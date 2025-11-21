package api.agendafacilpro.infraestructure.web.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        Integer status,
        String error,
        String message,
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

public record ValidationError(String field, String message) {}