package api.agendafacilpro.infraestructure.service;

import api.agendafacilpro.core.gateway.JwtTokenGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtTokenGatewayImpl implements JwtTokenGateway {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${api.security.token.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Override
    public String generateAccessToken(UUID userId, UUID organizationId, String email, List<String> permissions) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("orgId", organizationId.toString())
                .claim("email", email)
                .claim("permissions", permissions)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    @Override
    public UUID extractOrganizationId(String token) {
        String orgId = getClaims(token).get("orgId", String.class);
        return UUID.fromString(orgId);
    }

    @Override
    public List<String> extractPermissions(String token) {
        return getClaims(token).get("permissions", List.class);
    }

    // Método auxiliar para gerar a chave de assinatura
    private SecretKey getSigningKey() {
        // Se a chave for Base64, use Decoders.BASE64.decode(secret)
        return Decoders.BASE64.decode(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Método auxiliar para fazer o parse e validação
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}