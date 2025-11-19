package api.agendafacilpro.core.gateway;

public interface PasswordEncoderGateway {
    String encode(String plainPassword);
    boolean matches(String plainPassword, String encodedPassword);
}
