package com.jgmagalop.aidocanalyzer.documents.api;

import com.jgmagalop.aidocanalyzer.documents.domain.DocumentCategory;
import com.jgmagalop.aidocanalyzer.security.user.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DocumentUploadRequest(

        @NotBlank(message = "Debe indicar un título")
        @Size(max = 255, message = "El título no puede superar 255 caracteres")
        String title,

        @NotNull(message = "Debe indicar una categoría")
        DocumentCategory category,

        Department department
) {
}