package com.niluverse.uninex.auth;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A campus-hub account, created automatically the first time someone signs
 * in with Google. We never store a password -- Google is the only identity
 * provider -- so the fields here are limited to what we get back from the
 * OAuth2 userinfo endpoint plus our own role assignment.
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String googleId;
    private String email;
    private String name;
    private Role role = Role.STUDENT;
    private Instant createdAt = Instant.now();

    public User() {
    }

    public User(String googleId, String email, String name) {
        this.googleId = googleId;
        this.email = email;
        this.name = name;
        this.role = Role.STUDENT;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
