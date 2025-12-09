package api.agendafacilpro.core.domain.valueobjects;

import api.agendafacilpro.core.exceptions.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class Password {
    private final String hash;
    private final String plainPassword;

    public Password(String plainPassword) {
        if (plainPassword == null || plainPassword.length() < 8) {
            throw new ValidationException("A senha deve conter mais de 8 caracteres");
        }
        this.plainPassword = plainPassword;
        this.hash = plainPassword;
    }

    private Password(String hash, boolean isHashed) {
        this.hash = hash;
        this.plainPassword = null;
    }

    public static Password fromHash(String hash) {
        return new Password(hash, true);
    }

    public String getHash() {
        return hash;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public boolean matches(String plainPassword, PasswordEncoder encoder) {
        return encoder.matches(plainPassword, hash);
    }
}