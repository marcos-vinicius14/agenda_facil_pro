package api.agendafacilpro.core.usecases.output;

import api.agendafacilpro.core.domain.valueobjects.Email;

import java.util.UUID;

public record LoginOutput(
        UUID userId,
        UUID organizationId,
        String email,
        String acessToken,
        String refreshToken
) {
}
