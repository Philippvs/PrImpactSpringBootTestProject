package com.primpact.testproject.model;

import java.util.Set;

// CHANGE SCENARIO: SECURITY
public class UserModel {

    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Set<String> roles;
    private boolean enabled;

    public UserModel() {
        this.enabled = true;
    }

    public UserModel(String username, String email) {
        this();
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // SECURITY TEST POINT
    public boolean hasAdminRole() {
        return roles != null && roles.contains("ROLE_ADMIN");
    }
}
