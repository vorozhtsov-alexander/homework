package ru.vor.homework.security;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AuthController {

    public static final String X_AUTH_KEY = "X-Auth";
    final AuthService authService;
    final JwtUtils jwtUtils;

    public AuthController(final AuthService authService,
                          final JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/auth/login")
    public Mono<ResponseEntity<AuthUserDTO>> login(@Valid @RequestBody UserCredentials userData, ServerHttpResponse response)  {

        return authService.findAndRefreshToken(userData.getEmail(), userData.getPassword())
            .flatMap(user -> {
                var jwt = jwtUtils.createJwt(user.getEmail(), user.getRole());
                addRefreshTokenInCookie(response, user.getRefreshToken().toString());

                return Mono.just(ResponseEntity.ok(new AuthUserDTO(user, jwt)));
            })
            .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    private void addRefreshTokenInCookie(final ServerHttpResponse response, final String token) {
        var authCookie = ResponseCookie.fromClientResponse(X_AUTH_KEY, token)
            .maxAge(JwtUtils.JWT_EXPIRATION_TIME_IN_MINUTES * 60)
            .httpOnly(true)
            .path("/")
            .secure(false) //todo should be true in production
            .build();

        response.addCookie(authCookie);
    }

    @PostMapping("/auth/refresh-token")
    public Mono<ResponseEntity<AuthUserDTO>> refreshToken(ServerHttpRequest request, ServerHttpResponse response) {
        List<HttpCookie> refreshToken = request.getCookies().get(X_AUTH_KEY);

        if (refreshToken != null && !refreshToken.isEmpty()) {
            return authService.refreshToken(refreshToken.get(0).getValue())
                .flatMap(user -> {
                    var jwt = jwtUtils.createJwt(user.getEmail(), user.getRole());
                    addRefreshTokenInCookie(response, user.getRefreshToken().toString());

                    return Mono.just(ResponseEntity.ok(new AuthUserDTO(user, jwt)));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
        }

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/auth/revoke-token")
    public Mono<ResponseEntity<Void>> logoutUser(ServerHttpRequest request) {
        List<HttpCookie> refreshToken = request.getCookies().get(X_AUTH_KEY);

        if (refreshToken != null && !refreshToken.isEmpty()) {
            return authService.revokeRefreshToken(refreshToken.get(0).getValue())
                .flatMap(item -> Mono.just(ResponseEntity.status(HttpStatus.OK).build()));
        }

        return Mono.just(ResponseEntity.status(HttpStatus.OK).build());
    }
}
