package com.jgmagalop.aidocanalyzer.rag.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SearchQuery(

        @NotBlank(message = "Debe escribir una pregunta")
        @Size(max = 450, message = "La pregunta no puede superar 450 caracteres")
        String q
) {
}