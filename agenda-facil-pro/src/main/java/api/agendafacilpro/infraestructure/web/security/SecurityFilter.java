package api.agendafacilpro.infraestructure.web.security;

import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.exceptions.UserNotFoundException;
import api.agendafacilpro.core.gateway.JwtTokenGateway;
import api.agendafacilpro.core.gateway.UserGateway;
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

        String token = this.recoverTokenFromCookie(request);

        if (token != null && jwtTokenGateway.isTokenValid(token)) {

            UUID userId = jwtTokenGateway.extractUserId(token);

            User user = userGateway.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Usuário do token não encontrado na base de dados"));

            if (Boolean.TRUE.equals(user.getEnabled())) {
                List<String> permissions = userGateway.findPermissionsUserId(user.getId());

                var authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "access_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}