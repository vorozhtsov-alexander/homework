package ru.vor.homework;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.PublicResolverBuilder;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.vor.homework.user.UserResolver;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GraphqlController {

    private final UserResolver userResolver;

    private GraphQL graphQL;

    public GraphqlController(final UserResolver userResolver) {
        this.userResolver = userResolver;
    }

    @PostConstruct
    public void init(){
        GraphQLSchema schema = new GraphQLSchemaGenerator().withResolverBuilders(
            // Resolve by annotations
            new AnnotatedResolverBuilder(),
            // Resolve public methods inside root package
            new PublicResolverBuilder("ru.vor.homework"))
            .withOperationsFromSingleton(userResolver, UserResolver.class)
            .withValueMapperFactory(new JacksonValueMapperFactory()).generate();
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    @PostMapping(value = "/graphql")
    public ExecutionResult execute(@RequestBody Map<String, Object> request) throws GraphQLException {
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");
        if (variables == null) {
            variables = new HashMap<>();
        }
        return graphQL.execute(ExecutionInput.newExecutionInput()
            .query((String)request.get("query"))
            .operationName((String)request.get("operationName"))
            .variables(variables)
            .build());

    }
}
