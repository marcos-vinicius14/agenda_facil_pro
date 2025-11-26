package api.agendafacilpro.infraestructure.security.aspect;


import api.agendafacilpro.core.exceptions.ForbiddenException;
import api.agendafacilpro.infraestructure.security.annotations.HasPermission;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {


    @Before("@annotation(hasPermission)")
    public void checkPermission(HasPermission hasPermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Usuário não esta autenticado");
        }

        String requiredPermission = hasPermission.value();

        boolean hasAuthority = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredPermission));

        if (!hasAuthority) {
            throw new ForbiddenException("Permissões insuficientes. Requer: " + requiredPermission);
        }
    }
}