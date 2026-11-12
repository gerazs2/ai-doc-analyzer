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

    /** Extrae y limpia el texto. PDF: una sección por página. DOCX: una sola sección. */
    public List<Document> extract(Resource file, DocumentType type) {
        List<Document> sections = switch (type) {
            case PDF -> new PagePdfDocumentReader(file).get();
            case DOCX -> new TikaDocumentReader(file).get();
        };

        return sections.stream()
                .map(section -> new Document(clean(section.getText()), section.getMetadata()))
                .filter(section -> !section.getText().isBlank())
                .toList();
    }

    /** Quita espacios y líneas en blanco de sobra que deja el lector de PDF. */
    private String clean(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")   // varios espacios → uno
                .replaceAll(" *\\n *", "\n")              // sin espacios alrededor de los saltos
                .replaceAll("\\n{3,}", "\n\n")            // máximo una línea en blanco
                .strip();
    }
}