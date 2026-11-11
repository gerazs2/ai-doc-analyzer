package com.jgmagalop.aidocanalyzer.documents.api;

import com.jgmagalop.aidocanalyzer.documents.domain.DocumentCategory;
import com.jgmagalop.aidocanalyzer.documents.domain.DocumentStatus;
import com.jgmagalop.aidocanalyzer.documents.service.DocumentService;
import com.jgmagalop.aidocanalyzer.security.AuthenticatedUser;
import com.jgmagalop.aidocanalyzer.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public PageResponse<DocumentResponse> list(
            @RequestParam(required = false) DocumentCategory category,
            @RequestParam(required = false) DocumentStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal AuthenticatedUser user) {

        return documentService.page(category, status, user, pageable);
    }

    @GetMapping("/{id}")
    public DocumentResponse getById(@PathVariable Long id,
                                    @AuthenticationPrincipal AuthenticatedUser user) {
        return documentService.getById(id, user);
    }
}