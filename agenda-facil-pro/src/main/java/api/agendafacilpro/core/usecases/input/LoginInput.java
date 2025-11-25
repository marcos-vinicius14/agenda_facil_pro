package api.agendafacilpro.core.usecases.input;

import api.agendafacilpro.core.domain.valueobjects.Email;

public record LoginInput(
        Email email,
        String password
) {
}
