package com.jgmagalop.aidocanalyzer.ingestion;

import com.jgmagalop.aidocanalyzer.documents.domain.DocumentType;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentTextExtractor {

    /** Extrae el texto del archivo. PDF: un elemento por página. DOCX: un solo elemento. */
    public List<Document> extract(Resource file, DocumentType type) {
        return switch (type) {
            case PDF -> new PagePdfDocumentReader(file).get();
            case DOCX -> new TikaDocumentReader(file).get();
        };
    }
}