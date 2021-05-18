package ru.vor.homework.security;

import ru.vor.homework.user.User;

import java.util.UUID;

public class AuthUserDTO {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String jwtToken;

    public AuthUserDTO(){

    }

    public AuthUserDTO(User user, String jwt) {
        id = user.getId();
        email = user.getEmail();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        this.jwtToken = jwt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(final String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
