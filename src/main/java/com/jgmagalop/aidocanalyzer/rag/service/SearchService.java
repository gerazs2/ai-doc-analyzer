package com.jgmagalop.aidocanalyzer.rag.service;

import com.jgmagalop.aidocanalyzer.rag.api.SearchResult;
import com.jgmagalop.aidocanalyzer.security.AuthenticatedUser;
import com.jgmagalop.aidocanalyzer.security.user.Role;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private final VectorStore vectorStore;
    private final int topK;
    private final double similarityThreshold;
    private final String queryPrefix;
    private final String documentPrefix;

    public SearchService(VectorStore vectorStore,
                         @Value("${app.search.top-k}") int topK,
                         @Value("${app.search.similarity-threshold}") double similarityThreshold,
                         @Value("${app.embedding.query-prefix}") String queryPrefix,
                         @Value("${app.embedding.document-prefix}") String documentPrefix) {
        this.vectorStore = vectorStore;
        this.topK = topK;
        this.similarityThreshold = similarityThreshold;
        this.queryPrefix = queryPrefix;
        this.documentPrefix = documentPrefix;
    }

    /** Busca los fragmentos más parecidos a la pregunta entre los que el usuario puede ver. */
    public List<SearchResult> search(String query, AuthenticatedUser user) {
        SearchRequest.Builder request = SearchRequest.builder()
                .query(queryPrefix + query)
                .topK(topK)
                .similarityThreshold(similarityThreshold);

        if (user.role() != Role.ADMIN) {
            request.filterExpression(departmentFilter(user));
        }

        return vectorStore.similaritySearch(request.build()).stream()
                .map(this::toResult)
                .toList();
    }

    /** Solo fragmentos del departamento del usuario o de toda la empresa. */
    private Filter.Expression departmentFilter(AuthenticatedUser user) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        return b.or(
                b.eq("department", user.department().name()),
                b.eq("department", "GENERAL")
        ).build();
    }

    private SearchResult toResult(Document fragment) {
        Map<String, Object> metadata = fragment.getMetadata();

        // Quitar el prefijo del modelo: es una instrucción para el embedding, no parte del documento
        String text = fragment.getText();
        if (text != null && text.startsWith(documentPrefix)) {
            text = text.substring(documentPrefix.length());
        }

        return new SearchResult(
                toLong(metadata.get("document_id")),
                (String) metadata.get("title"),
                toInteger(metadata.get("page_number")),
                text,
                fragment.getScore() == null ? 0.0 : fragment.getScore()
        );
    }

    private static Long toLong(Object value) {
        return value instanceof Number n ? n.longValue() : null;
    }

    private static Integer toInteger(Object value) {
        return value instanceof Number n ? n.intValue() : null;
    }
}