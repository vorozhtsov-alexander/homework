package ru.vor.homework.user;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;

@Table
public class User {

    @PrimaryKey
    private UUID id;
    @Column
    @Indexed //todo add unique index
    private String email;
    @Column
    private String password;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    @Indexed
    private String role;
    @Column
    private ByteBuffer avatar; //todo move to separate table.
    @Column
    @Indexed
    private UUID refreshToken;
    @Column
    private Date expireTime;

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

    public ByteBuffer getAvatar() {
        return avatar;
    }

    public void setAvatar(final ByteBuffer avatar) {
        this.avatar = avatar;
    }

    public UUID getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(final UUID refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(final Date expireTime) {
        this.expireTime = expireTime;
    }
}
