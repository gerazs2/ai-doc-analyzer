package com.jgmagalop.aidocanalyzer.documents.domain;

import com.jgmagalop.aidocanalyzer.security.user.Department;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String originalFilename;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentCategory category;

    /** null = visible para toda la empresa (GENERAL). */
    @Enumerated(EnumType.STRING)
    private Department department;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false, unique = true)
    private String sha256;

    @Column(nullable = false)
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    private String errorMessage;

    private Integer chunkCount;

    @Column(nullable = false)
    private Long uploadedById;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant indexedAt;

    public Document(String title, String originalFilename, DocumentType documentType,
                    DocumentCategory category, Department department, long sizeBytes,
                    String sha256, String storagePath, Long uploadedById) {
        this.title = title;
        this.originalFilename = originalFilename;
        this.documentType = documentType;
        this.category = category;
        this.department = department;
        this.sizeBytes = sizeBytes;
        this.sha256 = sha256;
        this.storagePath = storagePath;
        this.uploadedById = uploadedById;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.status = DocumentStatus.PENDING;
    }

    public void markProcessing() {
        this.status = DocumentStatus.PROCESSING;
        this.errorMessage = null;
    }

    public void markIndexed(int chunkCount) {
        this.status = DocumentStatus.INDEXED;
        this.chunkCount = chunkCount;
        this.indexedAt = Instant.now();
        this.errorMessage = null;
    }

    public void markFailed(String errorMessage) {
        this.status = DocumentStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}