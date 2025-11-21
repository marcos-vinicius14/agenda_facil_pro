package api.agendafacilpro.infraestructure.web.dtos;

public record ValidationError(String field, String message) {
    public ValidationError(String message) {
        this(null, message);
    }
}
