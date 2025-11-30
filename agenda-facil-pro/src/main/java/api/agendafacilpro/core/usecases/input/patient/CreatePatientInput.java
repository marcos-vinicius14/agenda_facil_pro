package api.agendafacilpro.core.usecases.input.patient;

public record CreatePatientInput(
        String name,
        String email,
        String phone,
        String document
) {
}
