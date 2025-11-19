package api.agendafacilpro.infraestructure.security;

import api.agendafacilpro.core.gateway.PasswordEncoderGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderGateway implements PasswordEncoderGateway {
    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordEncoderGateway(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String encode(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String encodedPassword) {
        return encoder.matches(plainPassword, encodedPassword);
    }
}
