package api.agendafacilpro.core.usecases.output;

import java.util.UUID;

public record CreatePatientOutput(
        UUID patientId,
        String name,
        String email
) {
}
