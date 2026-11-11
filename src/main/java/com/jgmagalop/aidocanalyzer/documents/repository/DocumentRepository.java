package com.jgmagalop.aidocanalyzer.documents.repository;

import com.jgmagalop.aidocanalyzer.documents.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findBySha256(String sha256);
}