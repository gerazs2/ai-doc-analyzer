package com.jgmagalop.aidocanalyzer.ingestion;

import com.jgmagalop.aidocanalyzer.documents.domain.DocumentDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DocumentIndexCleanupListener {

    private final VectorStore vectorStore;

    public DocumentIndexCleanupListener(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @EventListener
    public void onDocumentDeleted(DocumentDeletedEvent event) {
        vectorStore.delete("document_id == " + event.documentId());
        log.info("Fragmentos del documento {} eliminados", event.documentId());
    }
}