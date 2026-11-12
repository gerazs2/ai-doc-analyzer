package com.jgmagalop.aidocanalyzer.ingestion;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextChunker {

    private final TokenTextSplitter splitter;

    public TextChunker(@Value("${app.ingestion.chunk-size}") int chunkSize) {
        this.splitter = TokenTextSplitter.builder()
                .withChunkSize(chunkSize)
                .build();
    }

    /** Divide las secciones extraídas en fragmentos. Cada fragmento conserva la metadata de su sección. */
    public List<Document> split(List<Document> sections) {
        return splitter.apply(sections);
    }
}