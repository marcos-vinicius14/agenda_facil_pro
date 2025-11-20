package api.agendafacilpro.core.usecases.input;


public record RegisterClinicInput(
        String organizationName,
        String document,
        String email,
        String password,
        String username
) {
}
