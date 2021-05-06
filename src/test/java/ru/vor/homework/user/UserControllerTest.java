package ru.vor.homework.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class UserControllerTest {

    @Autowired
    private WebTestClient webClient;

    //todo add test containers
//    @Test
    void uploadAvatar() {

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("avatar.jpg")).contentType(MediaType.MULTIPART_FORM_DATA);

        webClient.post()
            .uri("/users/upload-avatar/50aa0590-ae3d-11eb-b22b-f50dd1630873")
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isOk();
    }
}
