package com.jgmagalop.aidocanalyzer.documents.api;

import com.jgmagalop.aidocanalyzer.documents.domain.Document;
import com.jgmagalop.aidocanalyzer.documents.domain.DocumentCategory;
import com.jgmagalop.aidocanalyzer.documents.domain.DocumentStatus;
import com.jgmagalop.aidocanalyzer.documents.domain.DocumentType;
import com.jgmagalop.aidocanalyzer.security.user.Department;

import java.time.Instant;

public record DocumentResponse(
        Long id,
        String title,
        DocumentType documentType,
        DocumentCategory category,
        Department department,
        DocumentStatus status,
        Instant createdAt
) {

    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getDocumentType(),
                document.getCategory(),
                document.getDepartment(),
                document.getStatus(),
                document.getCreatedAt()
        );
    }
}