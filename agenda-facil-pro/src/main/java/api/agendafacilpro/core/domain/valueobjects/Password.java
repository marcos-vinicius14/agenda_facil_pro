package api.agendafacilpro.core.domain.valueobjects;

import api.agendafacilpro.core.exceptions.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class Password {
    private final String hash;

    public Password(String plainPassword) {
        if (plainPassword == null || plainPassword.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        this.hash = null;
    }

    private Password(String hash, boolean isHashed) {
        this.hash = hash;
    }

    public static Password fromHash(String hash) {
        return new Password(hash, true);
    }

    public String getHash() {
        return hash;
    }

    public boolean matches(String plainPassword, PasswordEncoder encoder) {
        return encoder.matches(plainPassword, hash);
    }
}