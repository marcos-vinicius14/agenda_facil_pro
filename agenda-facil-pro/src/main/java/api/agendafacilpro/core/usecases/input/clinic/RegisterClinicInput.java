package api.agendafacilpro.core.usecases.input.clinic;


public record RegisterClinicInput(
        String organizationName,
        String document,
        String email,
        String password,
        String username
) {
}
