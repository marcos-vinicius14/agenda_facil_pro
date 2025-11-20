package api.agendafacilpro.core.usecases.output;

import java.util.UUID;

public record RegisterClinicOutPut(
        UUID userId,
        UUID organizationId,
        String email,
        String accessToken,
        String refreshToken

) {}
