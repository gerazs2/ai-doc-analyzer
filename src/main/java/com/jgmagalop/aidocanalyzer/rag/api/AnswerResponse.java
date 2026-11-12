package com.jgmagalop.aidocanalyzer.rag.api;

import java.util.List;

public record AnswerResponse(String answer, List<SearchResult> sources) {
}