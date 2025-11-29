package api.agendafacilpro.infraestructure.gateway;

import api.agendafacilpro.core.domain.entities.User;
import api.agendafacilpro.core.domain.valueobjects.Email;
import api.agendafacilpro.core.exceptions.UserNotFoundException;
import api.agendafacilpro.core.exceptions.ValidationException;
import api.agendafacilpro.core.gateway.UserGateway;
import api.agendafacilpro.infraestructure.persistence.entities.UserJpaEntity;
import api.agendafacilpro.infraestructure.persistence.repository.UserJpaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryGateway implements UserGateway {

    private final UserJpaRepository repository;

    public UserRepositoryGateway(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public User create(User user) {
        UserJpaEntity entity = UserJpaEntity.fromDomain(user);
        UserJpaEntity savedEntity = repository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#user.id")
    public User update(User user) {
        UUID id = user.getId();

        if (id == null) {
            throw new ValidationException("Um usuário não pode ser atualizado sem ID.");
        }

        UserJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + id));

        entity.updateFromDomain(user);

        return repository.save(entity).toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.getValue())
                .map(UserJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public Optional<User> findById(UUID id) {
        System.out.println("--- Buscando Usuário no Banco de Dados: " + id + " ---");
        return repository.findById(id)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findPermissionsUserId(UUID id) {
        return repository.findPermissionsByUserId(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users-permissions", key = "#id")
    public List<String> findPermissionsByUserId(UUID id) {
        return repository.findPermissionsByUserId(id);
    }
}