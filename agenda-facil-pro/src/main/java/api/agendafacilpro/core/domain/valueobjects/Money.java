package api.agendafacilpro.core.domain.valueobjects;

import api.agendafacilpro.core.exceptions.ValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("O valor não pode ser negativo");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public Money(String amount) {
        this(new BigDecimal(amount));
    }

    public BigDecimal getAmount() { return amount; }

    public String getFormatted() {
        return String.format("R$ %.2f", amount);
    }
}