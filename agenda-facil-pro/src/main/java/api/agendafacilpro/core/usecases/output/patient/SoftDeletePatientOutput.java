package api.agendafacilpro.core.usecases.output.patient;

import java.util.UUID;

public record SoftDeletePatientOutput(
        UUID userId,
        Boolean isActive
) {
}
