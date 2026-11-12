package com.jgmagalop.aidocanalyzer.documents.api;

import com.jgmagalop.aidocanalyzer.documents.domain.DocumentCategory;
import com.jgmagalop.aidocanalyzer.documents.domain.DocumentType;
import com.jgmagalop.aidocanalyzer.security.user.Department;
import org.springframework.core.io.Resource;

public record DocumentForIngestion(
        Long id,
        String title,
        DocumentType type,
        DocumentCategory category,
        Department department,
        Resource file
) {
}