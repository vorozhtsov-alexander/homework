package ru.vor.homework.user;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(final PasswordEncoder passwordEncoder, final UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public Mono<UserDTO> create(UserDTO userData) {

        User user = new User();
        user.setId(Uuids.timeBased());
        user.setRole(userData.getRole());
        user.setEmail(userData.getEmail());
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        if (StringUtils.isNotEmpty(userData.getPassword())) {
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }

        return userRepository.save(user).flatMap(item -> Mono.just(new UserDTO(item)));
    }

    public Flux<UserDTO> getAll() {
        return userRepository.findAll().flatMap(item -> Flux.just(new UserDTO(item)));
    }

    public Mono<UserDTO> findById(UUID id) {
        return userRepository.findById(id).flatMap(item -> Mono.just(new UserDTO(item)));
    }

    public Mono<ByteBuffer> getAvatar(UUID id) {
        return userRepository.findById(id)
            .flatMap(item -> Mono.just(item.getAvatar()));
    }

    public Mono<UserDTO> update(UUID id,  UserDTO userData) {
        return userRepository.findById(id).flatMap(user -> {
            user.setEmail(userData.getEmail());
            user.setFirstName(userData.getFirstName());
            user.setLastName(userData.getLastName());
            user.setRole(userData.getRole());
            if (StringUtils.isNotEmpty(userData.getPassword()) &&
                !Objects.equals(userData.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(userData.getPassword()));
            }
            userRepository.save(user);
            return Mono.just(new UserDTO(user));
        });
    }

    public Mono<Void> delete(UUID id) {
        return userRepository.deleteById(id);
    }

    public Mono<Void> uploadAvatar(UUID id,  FilePart file) {

        return userRepository.findById(id)
            .flatMap(user -> file.content()
                .next() //todo check big file
                .flatMap(content -> {
                    user.setAvatar(content.asByteBuffer());
                    return userRepository.save(user);
                })).flatMap(item -> Mono.empty());
    }
}
