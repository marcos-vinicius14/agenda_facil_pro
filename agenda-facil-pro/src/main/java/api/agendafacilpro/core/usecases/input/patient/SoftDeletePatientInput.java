package api.agendafacilpro.core.usecases.input.patient;

import java.util.UUID;

public record SoftDeletePatientInput(
        UUID userId
) {
}
