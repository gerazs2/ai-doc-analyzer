package com.jgmagalop.aidocanalyzer.documents.storage;

import com.jgmagalop.aidocanalyzer.documents.domain.DocumentDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class DocumentFileCleanupListener {

    private final FileStorageService fileStorageService;

    public DocumentFileCleanupListener(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @TransactionalEventListener
    public void onDocumentDeleted(DocumentDeletedEvent event) {
        try {
            fileStorageService.delete(event.storagePath());
        } catch (Exception e) {
            log.warn("No se pudo borrar el archivo {} del documento {}",
                    event.storagePath(), event.documentId(), e);
        }
    }
}