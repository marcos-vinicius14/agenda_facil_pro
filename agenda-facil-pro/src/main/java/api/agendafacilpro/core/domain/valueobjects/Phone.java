package api.agendafacilpro.core.domain.valueobjects;


import api.agendafacilpro.core.exceptions.ValidationException;

public final class Phone {
    private final String value;

    public Phone(String rawValue) {
        String cleanValue = rawValue.replaceAll("[^\\d]", "");
        if (cleanValue.length() < 10 || cleanValue.length() > 11) {
            throw new ValidationException("Invalid phone number");
        }
        this.value = cleanValue;
    }

    public String getFormatted() {
        if (value.length() == 11) {
            return String.format("(%s) %s-%s", value.substring(0,2), value.substring(2,7), value.substring(7));
        }
        return String.format("(%s) %s-%s", value.substring(0,2), value.substring(2,6), value.substring(6));
    }

    public String getValue() { return value; }
}