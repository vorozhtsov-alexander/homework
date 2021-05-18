package ru.vor.homework.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Bean
    public ServerAuthenticationConverter jwtAuthenticationConverter() {

        return exchange -> Mono.justOrEmpty(exchange)
            .flatMap( request -> Mono.justOrEmpty(parseJwt(request.getRequest())))
            .map(token -> new UsernamePasswordAuthenticationToken(token, token));
    }

    private String parseJwt(ServerHttpRequest request) {
        List<String> authorizationList = request.getHeaders().get("Authorization");

        if (authorizationList == null || authorizationList.isEmpty()){
            return null;
        }

        String headerAuth = authorizationList.get(0);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

    @Bean
    public ReactiveAuthenticationManager jwtAuthenticationManager(JwtUtils jwtUtils) {

        return authentication -> Mono.justOrEmpty(authentication)
            .filter(item -> item.getCredentials() instanceof String && jwtUtils.validateJwt((String) item.getCredentials()))
            .map(item -> {

                String token = (String) item.getCredentials();
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                String role = jwtUtils.getRoleFromJwtToken(token);

                if (!org.apache.commons.lang3.StringUtils.isBlank(role)) {
                    authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
                }

                return new UsernamePasswordAuthenticationToken(
                    jwtUtils.getUserNameFromJwtToken(token),
                    token,
                    authorities
                );
            });
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager jwtAuthenticationManager,
                                                         ServerAuthenticationConverter jwtAuthenticationConverter) {

        var authenticationWebFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter);

        return http.authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers("/users/download-avatar/**").permitAll()
            .pathMatchers("/auth/**")
                .permitAll()
            .pathMatchers("/users/**")
                .hasAuthority(ROLE_ADMIN)
            .pathMatchers("/graphql")
                .hasAuthority(ROLE_ADMIN)
            .anyExchange().authenticated()
            .and()
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .httpBasic()
                .disable()
            .csrf()
                .disable()
            .formLogin()
                .disable()
            .logout()
                .disable()
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}