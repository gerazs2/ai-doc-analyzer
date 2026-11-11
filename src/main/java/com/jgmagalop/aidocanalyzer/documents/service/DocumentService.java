package com.jgmagalop.aidocanalyzer.documents.service;

import com.jgmagalop.aidocanalyzer.documents.api.DocumentResponse;
import com.jgmagalop.aidocanalyzer.documents.api.DocumentUploadRequest;
import com.jgmagalop.aidocanalyzer.documents.domain.*;
import com.jgmagalop.aidocanalyzer.documents.repository.DocumentRepository;
import com.jgmagalop.aidocanalyzer.documents.storage.FileStorageService;
import com.jgmagalop.aidocanalyzer.security.AuthenticatedUser;
import com.jgmagalop.aidocanalyzer.security.user.Role;
import com.jgmagalop.aidocanalyzer.shared.api.PageResponse;
import com.jgmagalop.aidocanalyzer.shared.exception.DuplicateResourceException;
import com.jgmagalop.aidocanalyzer.shared.exception.InvalidFileException;
import com.jgmagalop.aidocanalyzer.shared.exception.ResourceNotFoundException;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class DocumentService {

    private static final String PDF_MIME = "application/pdf";
    private static final String DOCX_MIME =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;
    private final ApplicationEventPublisher eventPublisher;
    private final Tika tika = new Tika();

    public DocumentService(DocumentRepository documentRepository,
                           FileStorageService fileStorageService,
                           ApplicationEventPublisher eventPublisher) {
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public DocumentResponse register(MultipartFile file, DocumentUploadRequest request, Long uploadedById) {
        // 1. Archivo no vacío
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("El archivo está vacío");
        }

        // 2. Tipo real del contenido
        DocumentType type = detectType(file);

        // 3. Hash del contenido
        String sha256 = computeSha256(file);

        // 4. Duplicados
        documentRepository.findBySha256(sha256).ifPresent(existing -> {
            throw new DuplicateResourceException(
                    "El archivo ya existe como \"" + existing.getTitle() + "\" (id " + existing.getId() + ")");
        });

        // 5. Guardar en disco con un nombre generado por nosotros
        String filename = sha256 + "." + type.name().toLowerCase();
        String storagePath = fileStorageService.store(file, filename);

        // 6. Guardar en la base; si falla, borrar el archivo para no dejar huérfanos
        try {
            Document document = documentRepository.saveAndFlush(new Document(
                    request.title().trim(),
                    originalFilename(file),
                    type,
                    request.category(),
                    request.department(),
                    file.getSize(),
                    sha256,
                    storagePath,
                    uploadedById));

            // 7. Avisar que hay un documento nuevo para procesar (Fase 3)
            eventPublisher.publishEvent(new DocumentUploadedEvent(document.getId()));

            // 8. Responder
            return DocumentResponse.from(document);

        } catch (RuntimeException e) {
            fileStorageService.delete(storagePath);
            throw e;
        }
    }

    private DocumentType detectType(MultipartFile file) {
        try (TikaInputStream in = TikaInputStream.get(file.getInputStream())) {
            String mime = tika.detect(in, file.getOriginalFilename());
            return switch (mime) {
                case PDF_MIME -> DocumentType.PDF;
                case DOCX_MIME -> DocumentType.DOCX;
                default -> throw new InvalidFileException("Solo se permiten archivos PDF y DOCX");
            };
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo", e);
        }
    }

    private String computeSha256(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo", e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no está disponible", e);
        }
    }

    private String originalFilename(MultipartFile file) {
        String name = StringUtils.cleanPath(String.valueOf(file.getOriginalFilename()));
        return name.length() > 255 ? name.substring(0, 255) : name;
    }

    @Transactional(readOnly = true)
    public PageResponse<DocumentResponse> page(DocumentCategory category,
                                                DocumentStatus status,
                                                AuthenticatedUser user,
                                                Pageable pageable) {
        boolean allDepartments = user.role() == Role.ADMIN;
        Page<Document> documents = documentRepository.search(
                category, status, user.department(), allDepartments, pageable);

        return  PageResponse.from(documents.map(DocumentResponse::from));

    }

    @Transactional(readOnly = true)
    public DocumentResponse getById(Long id, AuthenticatedUser user) {
        return documentRepository.findById(id)
                .filter(document -> canView(document, user))
                .map(DocumentResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado"));
    }

    private boolean canView(Document document, AuthenticatedUser user) {
        return user.role() == Role.ADMIN
                || document.getDepartment() == null
                || document.getDepartment() == user.department();
    }
}