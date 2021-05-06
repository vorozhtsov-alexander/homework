package ru.vor.homework.user;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

public class UserDTO {

    private UUID id;
    @NotBlank(message = "Email is mandatory")
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean avatar = true;

    public UserDTO(){

    }

    public UserDTO(User user) {
        id = user.getId();
        email = user.getEmail();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        role = user.getRole();
        avatar = user.getAvatar() != null;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
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

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public Boolean isAvatar() {
        return avatar;
    }

    public void setAvatar(final Boolean avatar) {
        this.avatar = avatar;
    }
}
