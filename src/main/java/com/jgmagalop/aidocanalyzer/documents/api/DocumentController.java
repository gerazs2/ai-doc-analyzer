package com.jgmagalop.aidocanalyzer.documents.api;

import com.jgmagalop.aidocanalyzer.documents.service.DocumentService;
import com.jgmagalop.aidocanalyzer.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute DocumentUploadRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {

        DocumentResponse response = documentService.register(file, request, user.id());
        return ResponseEntity.accepted().body(response);
    }
}