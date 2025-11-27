package api.agendafacilpro.core.usecases.input;

public record CreatePatientInput(
        String name,
        String email,
        String phone,
        String document
) {
}
