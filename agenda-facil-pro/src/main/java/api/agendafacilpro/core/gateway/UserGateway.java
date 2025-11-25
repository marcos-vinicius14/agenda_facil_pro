package api.agendafacilpro.core.gateway;

import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.domain.valueobjects.Email;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserGateway {
    User create(User user);
    User update(User user);
    Optional<User> findByEmail(Email email);
    Optional<User> findById(UUID id);
    boolean existsByEmail(String email);
    List<String> findPermissionsUserId(UUID id);

    List<String> findPermissionsByUserId(UUID id);
}
