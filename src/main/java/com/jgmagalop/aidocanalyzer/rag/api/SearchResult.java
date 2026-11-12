package com.jgmagalop.aidocanalyzer.rag.api;

public record SearchResult(
        Long documentId,
        String title,
        Integer page,
        String text,
        double score
) {
}
