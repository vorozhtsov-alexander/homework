package ru.vor.homework.user;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = UserResolverTest.Initializer.class)
@Testcontainers
public class UserResolverTest {

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            CassandraContainer<?> cassandraContainer =
                new CassandraContainer<>("cassandra:3")
                    .withExposedPorts(9042);

            cassandraContainer.start();

            TestPropertyValues.of(
                "cassandra.contactpoints=" + cassandraContainer.getContainerIpAddress(),
                "cassandra.port=" + cassandraContainer.getMappedPort(9042)
            ).applyTo(applicationContext);
        }
    }

    @Autowired
    private WebTestClient webClient;

    @Test
    void baseTest() throws Exception {

        //create user
        var query = "mutation{\n" +
            "  createUser(details:{email : \"test email\", firstName : \"first name\", lastName : \"last name\", role : \"ADMIN\"}) {\n" +
            "    id,\n" +
            "    firstName\n" +
            "    email\n, " +
            "  }\n" +
            "}";

        var entityExchangeResult = webClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .body(generateRequest(query, null))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.createUser.firstName").isEqualTo("first name")
            .jsonPath("$.createUser.email").isEqualTo("test email")
            .returnResult();

        var result = new String(Objects.requireNonNull(entityExchangeResult.getResponseBody()));

        Map user = (Map) new ObjectMapper().readValue(result, Map.class).get("createUser");
        String id = (String) user.get("id");

        // get user list
        query = "{\n" +
            "  getAllUsers {\n" +
            "    id,\n" +
            "    email,\n" +
            "    firstName,\n" +
            "    lastName,\n" +
            "    role\n" +
            "  }\n" +
            "}";

        webClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .body(generateRequest(query, null))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.getAllUsers[0].firstName").isEqualTo("first name")
            .jsonPath("$.getAllUsers[0].email").isEqualTo("test email");

        //upload avatar
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("avatar.jpg")).contentType(MediaType.MULTIPART_FORM_DATA);

        webClient.post()
            .uri("/users/upload-avatar/" + id)
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isOk();

        //download avatar
        entityExchangeResult = webClient.get()
            .uri("/users/download-avatar/" + id)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .returnResult();

        Assertions.assertEquals(56054, Objects.requireNonNull(entityExchangeResult.getResponseBody()).length);
    }

    private BodyInserter<String, ReactiveHttpOutputMessage> generateRequest(String query, Map<String, Object> variables) throws JSONException {
        var jsonObject = new JSONObject();

        jsonObject.put("query", query);

        if (variables != null) {
            jsonObject.put("variables", Collections.singletonMap("input", variables));
        }

        return BodyInserters.fromValue(jsonObject.toString());
    }
}
