package api.agendafacilpro.core.usecases.output;

import java.util.UUID;

public record ListPatientOutput(
        UUID id,
        String name,
        String email,
        String phone,
        String document,
        Boolean isActive
) {
}
