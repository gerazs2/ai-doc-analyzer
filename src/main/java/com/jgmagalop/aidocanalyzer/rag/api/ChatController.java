package com.jgmagalop.aidocanalyzer.rag.api;

import com.jgmagalop.aidocanalyzer.rag.service.RagService;
import com.jgmagalop.aidocanalyzer.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final RagService ragService;

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public AnswerResponse ask(@Valid @RequestBody AskRequest request,
                              @AuthenticationPrincipal AuthenticatedUser user) {
        return ragService.ask(request.question().trim(), user);
    }
}