package api.agendafacilpro.core.domain.valueobjects;

import api.agendafacilpro.core.exceptions.ValidationException;

public final class DurationMinutes {
    private final Integer value;

    public DurationMinutes(Integer value) {
        if (value == null || value < 15 || value > 480) {
            throw new ValidationException("A duração miníma de uma consulta deve estar entre 15 e 480 minutos");
        }
        this.value = value;
    }

    public Integer getValue() { return value; }
}