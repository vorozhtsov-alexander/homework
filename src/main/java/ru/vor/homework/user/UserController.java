package ru.vor.homework.user;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class UserController {

    final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Flux<UserDTO> getAll() {
        return userService.getAll();
    }

    @PostMapping("/users/create")
    public Mono<UserDTO> create(@Valid @RequestBody UserDTO user) {
        return userService.create(user);
    }

    @PutMapping("/users/{id}")
    public Mono update(@PathVariable UUID id, @Valid @RequestBody UserDTO user) {
        return userService.update(id, user)
            .map(updatedUser -> new ResponseEntity<>(updatedUser, HttpStatus.OK))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        try {
            userService.delete(id).subscribe();
        } catch (Exception e) {
            return new ResponseEntity<>("Fail to delete!", HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>("User has been deleted!", HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public Mono<UserDTO> findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @PostMapping(value = "/users/upload-avatar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<Void> uploadAvatar(@PathVariable UUID id, @RequestPart("file") FilePart file) {
        return userService.uploadAvatar(id, file);
    }

    @GetMapping("/users/download-avatar/{id}")
    public Mono<Void> downloadAvatar(@PathVariable UUID id, ServerHttpResponse response) {

        return userService.getAvatar(id).flatMap(item -> {

            ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
            response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+id+".jpg");
            response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);

            Mono<DataBuffer> body = Mono.just(zeroCopyResponse.bufferFactory().wrap(item.array()));
            return zeroCopyResponse.writeWith(body);
        });
    }
}
