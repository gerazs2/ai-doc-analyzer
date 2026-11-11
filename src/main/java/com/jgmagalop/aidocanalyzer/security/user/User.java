package com.jgmagalop.aidocanalyzer.security.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Instant tokensValidSince;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public User(String email, String passwordHash, String firstName, String lastName,
                Role role, Department department) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.department = department;
        this.active = true;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.tokensValidSince = now;
    }

    // --- Cambios que invalidan los tokens emitidos antes ---

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        invalidateTokens();
    }

    public void changeRole(Role newRole) {
        this.role = newRole;
        invalidateTokens();
    }

    public void changeDepartment(Department newDepartment) {
        this.department = newDepartment;
        invalidateTokens();
    }

    public void deactivate() {
        this.active = false;
        invalidateTokens();
    }

    private void invalidateTokens() {
        this.tokensValidSince = Instant.now();
    }

    public void rename(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}