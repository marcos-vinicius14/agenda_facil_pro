package api.agendafacilpro.infraestructure.web;

import api.agendafacilpro.core.exceptions.ForbiddenException;
import api.agendafacilpro.core.exceptions.UserNotFoundException;
import api.agendafacilpro.core.exceptions.ValidationException; // Sua exceção de Domínio
import api.agendafacilpro.infraestructure.web.dtos.response.ErrorResponse;
import api.agendafacilpro.infraestructure.web.dtos.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidation(ValidationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        var response = ErrorResponse.simple(
                status.value(),
                "Erro de validação",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        var response = ErrorResponse.simple(
                status.value(),
                "Recurso não encontrado",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<ValidationError> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        var response = ErrorResponse.withValidation(
                status.value(),
                "Erro de validção",
                "Os dados inseridos são inválidos. Por favor, corrija e tente novamente.",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        var response = ErrorResponse.simple(
                status.value(),
                "Erro interno",
                "Um erro inesperado aconteceu. Contate o suporte ou tente novamente mais tarde.",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        var response = ErrorResponse.simple(
                status.value(),
                "Acesso negado",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }


}