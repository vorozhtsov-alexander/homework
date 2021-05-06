package ru.vor.homework.user;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.netty.handler.codec.http.multipart.FileUpload;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

//todo current solution doesn't work with webflux well. weekend?! :)
@Component
public class UserResolver {

    private final UserService userService;

    public UserResolver(final UserService userService) {
        this.userService = userService;
    }

    //todo it seems that validation works but doesn't show friendly message
    @GraphQLMutation(name="createUser")
    public UserDTO createUser(@GraphQLArgument(name="details") @Valid UserDTO user) {
        return userService.create(user).block();
    }

    @GraphQLQuery(name="getAllUsers")
    public List<UserDTO> getAllUsers() {
        return userService.getAll().collectList().block();
    }

    @GraphQLMutation(name="updateUser")
    public UserDTO updateUser(@GraphQLArgument(name="details") @Valid UserDTO user) {
        return userService.update(user.getId(), user).block();
    }

    @GraphQLMutation(name="deleteUser")
    public Boolean deleteUser(@GraphQLArgument(name="id") UUID id) {
        userService.delete(id);
        return true;
    }

    @GraphQLQuery(name="getUser")
    public UserDTO getUser(@GraphQLArgument(name="id") UUID id) {
        return userService.findById(id).block();
    }
}
