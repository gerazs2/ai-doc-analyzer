package com.jgmagalop.aidocanalyzer.rag.api;

import com.jgmagalop.aidocanalyzer.rag.service.SearchService;
import com.jgmagalop.aidocanalyzer.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<SearchResult> search(@Valid @ModelAttribute SearchQuery query,
                                     @AuthenticationPrincipal AuthenticatedUser user) {
        return searchService.search(query.q().trim(), user);
    }
}