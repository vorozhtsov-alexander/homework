package ru.vor.homework.user;

import com.datastax.dse.driver.internal.core.graph.ByteBufUtil;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
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
        return userRepository.findById(id).flatMap(item -> Mono.just(item.getAvatar()));
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

    public void uploadAvatar(UUID id, MultipartFile file) throws IOException {

        User user = userRepository.findById(id).block();
        if (user != null) {
            user.setAvatar(ByteBuffer.wrap(file.getInputStream().readAllBytes()));
            userRepository.save(user).block();
        }
    }

//    public Mono<Void> uploadAvatar(UUID id,  Mono<FilePart> file) {
//
//        return userRepository.findById(id).flatMap(user -> {
//
//            DataBuffer buffer = DefaultDataBufferFactory.sharedInstance.allocateBuffer();
//
//            return file.flatMap(filePart -> {
//                filePart.content()
//                        .doOnNext(buffer::write)
//                        .doOnComplete(() -> {
//                            user.setAvatar(buffer.asByteBuffer());
//                            userRepository.save(user);
//                        });
//                    return Mono.empty();
//                }
//            );
//        });
//    }
}
