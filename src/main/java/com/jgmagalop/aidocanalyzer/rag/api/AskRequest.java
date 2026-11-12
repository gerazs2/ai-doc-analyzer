package com.jgmagalop.aidocanalyzer.rag.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AskRequest(
        @NotBlank(message = "Debe escribir una pregunta")
        @Size(max = 1000, message = "La pregunta no puede superar 1000 caracteres")
        String question
) {
}