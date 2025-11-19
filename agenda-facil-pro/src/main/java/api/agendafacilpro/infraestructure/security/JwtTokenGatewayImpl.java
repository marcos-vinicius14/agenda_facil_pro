package api.agendafacilpro.infraestructure.security;

import api.agendafacilpro.core.gateway.JwtTokenGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JwtTokenGatewayImpl implements JwtTokenGateway {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.accessTokenExpiration}")
    private Long accessTokenExpiration;

    @Value("${security.jwt.refreshTokenExpiration}")
    private Long refreshTokenExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String generateAccessToken(UUID userId, UUID organizationId, String email, List<String> permissions) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("orgId", organizationId)
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
    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaims(token).getSubject());
    }

    @Override
    public UUID extractOrganizationId(String token) {
        String orgId = extractClaims(token).get("orgId", String.class);
        if (orgId != null) {
            return UUID.fromString(orgId);
        }
        return null;
    }

    @Override
    public List<String> extractPermissions(String token) {
        return extractClaims(token).get("permissions", List.class);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
