package api.agendafacilpro.infraestructure.web.security;

import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.exceptions.UserNotFoundException;
import api.agendafacilpro.core.gateway.JwtTokenGateway;
import api.agendafacilpro.core.gateway.UserGateway;
import api.agendafacilpro.core.multitenancy.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtTokenGateway jwtTokenGateway;
    private final UserGateway userGateway;

    public SecurityFilter(JwtTokenGateway jwtTokenGateway, UserGateway userGateway) {
        this.jwtTokenGateway = jwtTokenGateway;
        this.userGateway = userGateway;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = this.recoverToken(request);

            if (token != null && jwtTokenGateway.isTokenValid(token)) {
                UUID userId = jwtTokenGateway.extractUserId(token);

                User user = userGateway.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

                if (Boolean.TRUE.equals(user.getEnabled())) {
                    List<String> permissions = userGateway.findPermissionsUserId(user.getId());
                    var authorities = permissions.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    TenantContext.setTenant(user.getOrganizationId());
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    private String recoverToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "access_token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        var authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}