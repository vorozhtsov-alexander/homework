package ru.vor.homework.user;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = UserResolverTest.Initializer.class)
@AutoConfigureMockMvc()
public class UserResolverTest {

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            GenericContainer<?> cassandra =
                new GenericContainer<>("cassandra:3").withExposedPorts(9042);

            cassandra.start();

            TestPropertyValues.of(
                "cassandra.contactpoints=" + cassandra.getContainerIpAddress(),
                "cassandra.port=" + cassandra.getMappedPort(9042)
            ).applyTo(applicationContext);
        }
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebTestClient webClient;

    @Test
    void simpleTest() throws Exception {

        //create user

        var query = "mutation{\n" +
            "  createUser(details:{email : \"email\", firstName : \"first name\", lastName : \"last name\", role : \"ADMIN\"}) {\n" +
            "    id,\n" +
            "    firstName\n" +
            "  }\n" +
            "}";

        var postResult = performGraphQlPost(query, null);

        // Then
        String result = postResult.andExpect(status().isOk())
            .andExpect(jsonPath("$.errors").doesNotExist())
            .andExpect(jsonPath("$.createUser.firstName").value("first name"))
            .andReturn().getResponse().getContentAsString();

        Map user = (Map) new ObjectMapper().readValue(result, Map.class).get("createUser");
        String id = (String) user.get("id");

        //upload avatar
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("avatar.jpg")).contentType(MediaType.MULTIPART_FORM_DATA);

        webClient.post()
            .uri("/users/upload-avatar/" + id)
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isOk();
    }

    private ResultActions performGraphQlPost(String query, Map<String, Object> variables) throws Exception {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .post("/graphql")
                .content(generateRequest(query, variables))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    private String generateRequest(String query, Map<String, Object> variables) throws JSONException {
        var jsonObject = new JSONObject();

        jsonObject.put("query", query);

        if (variables != null) {
            jsonObject.put("variables", Collections.singletonMap("input", variables));
        }

        return jsonObject.toString();
    }
}
