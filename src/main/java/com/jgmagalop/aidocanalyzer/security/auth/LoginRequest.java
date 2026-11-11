package com.jgmagalop.aidocanalyzer.security.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El email es obliatorio")
        @Email(message = "Debe ser un email valido")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
