package api.agendafacilpro.core.usecases.input.auth;

import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.core.domain.valueobjects.Password;

public record LoginInput(
        Email email,
        Password password
) {
}
