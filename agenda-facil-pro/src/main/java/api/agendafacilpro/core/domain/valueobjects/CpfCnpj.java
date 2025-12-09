package api.agendafacilpro.core.domain.valueobjects;

import api.agendafacilpro.core.exceptions.ValidationException;

public final class CpfCnpj {
    private final String value;
    private final Type type;

    public enum Type { CPF, CNPJ }

    public CpfCnpj(String rawValue) {
        String cleanValue = rawValue.replaceAll("[^\\d]", "");
        this.type = cleanValue.length() == 11 ? Type.CPF : Type.CNPJ;

        if (!isValid(cleanValue)) {
            throw new ValidationException("Invalid CPF/CNPJ");
        }
        this.value = cleanValue;
    }

    private boolean isValid(String cleanValue) {
        if (cleanValue.length() == 11) return isValidCPF(cleanValue);
        if (cleanValue.length() == 14) return isValidCNPJ(cleanValue);
        return false;
    }

    private boolean isValidCPF(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        char[] digits = cpf.toCharArray();
        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += (digits[i] - '0') * (10 - i);
        }
        int check1 = 11 - (sum1 % 11);
        if (check1 == 10 || check1 == 11) {
            check1 = 0;
        }
        if (check1 != (digits[9] - '0')) {
            return false;
        }
        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += (digits[i] - '0') * (11 - i);
        }
        int check2 = 11 - (sum2 % 11);
        if (check2 == 10 || check2 == 11) {
            check2 = 0;
        }
        return check2 == (digits[10] - '0');
    }

    private boolean isValidCNPJ(String cnpj) {
        int[] digits = cnpj.chars().map(c -> c - '0').toArray();
        int[] weights1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int[] weights2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};

        int sum1 = 0, sum2 = 0;
        for (int i = 0; i < 12; i++) sum1 += digits[i] * weights1[i];
        for (int i = 0; i < 13; i++) sum2 += digits[i] * weights2[i];

        int check1 = sum1 % 11 < 2 ? 0 : 11 - (sum1 % 11);
        int check2 = sum2 % 11 < 2 ? 0 : 11 - (sum2 % 11);

        return check1 == digits[12] && check2 == digits[13];
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getFormatted() {
        if (type == Type.CPF) {
            return String.format("%s.%s.%s-%s",
                    value.substring(0,3), value.substring(3,6),
                    value.substring(6,9), value.substring(9,11));
        }
        return String.format("%s.%s.%s/%s-%s",
                value.substring(0,2), value.substring(2,5),
                value.substring(5,8), value.substring(8,12),
                value.substring(12,14));
    }
}