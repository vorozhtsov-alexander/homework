package ru.vor.homework.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CORSFilter implements WebFilter {

    /*
    // Disabling CORS for the purpose of Demo.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange,
                             WebFilterChain webFilterChain) {

        HttpHeaders headers = serverWebExchange.getResponse().getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        headers.add("Access-Control-Max-Age", "3600");
        headers.add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        headers.add("Access-Control-Expose-Headers", "Location");
        return webFilterChain.filter(serverWebExchange);
    }
}
