package api.agendafacilpro.core.gateway;

import api.agendafacilpro.core.domain.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserGateway {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    boolean existsByEmail(String email);
    List<String> findPermissionsUserId(UUID id);
}
