package ru.vor.homework.user;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCassandraRepository<User, UUID> {

    Mono<User> findByEmail(String email);
    Mono<User> findByRefreshToken(UUID token);
    Mono<User> findByRole(String email);
}
