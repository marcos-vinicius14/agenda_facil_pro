package api.agendafacilpro.core.gateway;

import java.util.List;
import java.util.UUID;

public interface JwtTokenGateway {
    String generateAccessToken(UUID userId, UUID organizationId, String email, List<String> permissions);
    String generateRefreshToken(UUID userId);
    UUID extractUserId(String token);
    UUID extractOrganizationId(String token);
    List<String> extractPermissions(String token);
    boolean isTokenValid(String token);
}
