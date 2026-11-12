package com.jgmagalop.aidocanalyzer.documents.domain;

public record DocumentDeletedEvent(Long documentId, String storagePath) {
}
