package ru.vor.homework.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.vor.homework.user.User;
import ru.vor.homework.user.UserRepository;

import javax.annotation.Nullable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(final PasswordEncoder passwordEncoder, final UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public Mono<User> findAndRefreshToken(String email, String password) {
        return userRepository.findByEmail(email)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .flatMap(user -> {
                java.util.Date expireTime = Date.from(LocalDateTime.now()
                    .plus(1, ChronoUnit.DAYS)
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
                user.setExpireTime(expireTime);
                user.setRefreshToken(UUID.randomUUID());
                return userRepository.save(user);
            })
            .flatMap(item -> Mono.just(item))
            .switchIfEmpty(Mono.empty());
    }

    public Mono<User> refreshToken(@Nullable String token) {

        if (StringUtils.isBlank(token)) {
            return Mono.empty();
        }

        return userRepository.findByRefreshToken(UUID.fromString(token)) //todo add filtration by expire time
            .flatMap(user -> {
                java.util.Date expireTime = Date.from(LocalDateTime.now()
                    .plus(1, ChronoUnit.DAYS)
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
                user.setExpireTime(expireTime);
                user.setRefreshToken(UUID.randomUUID());
                return userRepository.save(user);
            })
            .flatMap(Mono::just)
            .switchIfEmpty(Mono.empty());
    }

    public Mono<Void> revokeRefreshToken(@Nullable String token) {

        if (StringUtils.isBlank(token)) {
            return Mono.empty();
        }

        return userRepository.findByRefreshToken(UUID.fromString(token))
            .flatMap(user -> {
                user.setRefreshToken(null);
                return userRepository.save(user);
            })
            .flatMap(item -> Mono.<Void>empty())
            .switchIfEmpty(Mono.empty());
    }
}
