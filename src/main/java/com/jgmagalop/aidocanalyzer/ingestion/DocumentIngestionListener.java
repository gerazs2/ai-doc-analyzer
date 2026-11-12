package com.jgmagalop.aidocanalyzer.ingestion;

import com.jgmagalop.aidocanalyzer.documents.api.DocumentForIngestion;
import com.jgmagalop.aidocanalyzer.documents.domain.DocumentUploadedEvent;
import com.jgmagalop.aidocanalyzer.documents.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DocumentIngestionListener {

    private final DocumentService documentService;
    private final DocumentTextExtractor textExtractor;
    private final TextChunker textChunker;
    private final VectorStore vectorStore;

    public DocumentIngestionListener(DocumentService documentService,
                                     DocumentTextExtractor textExtractor,
                                     TextChunker textChunker,
                                     VectorStore vectorStore) {
        this.documentService = documentService;
        this.textExtractor = textExtractor;
        this.textChunker = textChunker;
        this.vectorStore = vectorStore;
    }

    @Async
    @TransactionalEventListener
    public void onDocumentUploaded(DocumentUploadedEvent event) {
        Long documentId = event.documentId();

        // Paso 1: marcar PROCESSING y obtener el archivo
        DocumentForIngestion document = documentService.startProcessing(documentId);

        try {
            // Paso 2: extraer el texto
            List<org.springframework.ai.document.Document> pages =
                    textExtractor.extract(document.file(), document.type());

            // Paso 3: partir en fragmentos (sin texto = PDF escaneado o vacío)
            List<org.springframework.ai.document.Document> chunks = textChunker.split(pages);
            if (chunks.isEmpty()) {
                documentService.failProcessing(documentId,
                        "No se encontró texto en el documento (¿es un PDF escaneado?)");
                log.warn("Documento {} sin texto extraíble", documentId);
                return;
            }

            // Paso 4: agregar metadata
            addMetadata(chunks, document);

            // Paso 5: generar embeddings y guardar en vector_store
            vectorStore.add(chunks);

            documentService.completeProcessing(documentId, chunks.size());
            log.info("Documento {} ({}) indexado: {} fragmento(s)",
                    documentId, document.title(), chunks.size());

        } catch (Exception e) {
            log.error("Error al procesar el documento {}", documentId, e);
            documentService.failProcessing(documentId, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /** Agrega a cada fragmento los datos para citar, filtrar por permisos y borrar. */
    private void addMetadata(List<org.springframework.ai.document.Document> chunks,
                             DocumentForIngestion document) {
        String department = document.department() == null ? "GENERAL" : document.department().name();

        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> metadata = chunks.get(i).getMetadata();
            metadata.put("document_id", document.id());
            metadata.put("title", document.title());
            metadata.put("category", document.category().name());
            metadata.put("department", department);
            metadata.put("chunk_index", i);
        }
    }
}