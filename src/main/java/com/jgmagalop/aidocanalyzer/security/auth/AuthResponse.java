package com.jgmagalop.aidocanalyzer.security.auth;

public record AuthResponse(String token, String fullName, String role) {
}