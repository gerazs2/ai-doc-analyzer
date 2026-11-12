package com.jgmagalop.aidocanalyzer.rag.service;

import com.jgmagalop.aidocanalyzer.rag.api.AnswerResponse;
import com.jgmagalop.aidocanalyzer.rag.api.SearchResult;
import com.jgmagalop.aidocanalyzer.security.AuthenticatedUser;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    static final String NO_INFO_ANSWER = "No encontré información sobre eso en los documentos disponibles.";

    private final ChatClient chatClient;
    private final SearchService searchService;
    private final Resource systemPrompt;

    public RagService(ChatClient.Builder chatClientBuilder,
                      SearchService searchService,
                      @Value("classpath:prompts/rag-system.st") Resource systemPrompt) {
        this.chatClient = chatClientBuilder.build();
        this.searchService = searchService;
        this.systemPrompt = systemPrompt;
    }

    public AnswerResponse ask(String question, AuthenticatedUser user) {
        // 1. Recuperar los fragmentos que el usuario puede ver
        List<SearchResult> sources = searchService.search(question, user);

        // Sin fragmentos no hay nada que preguntarle al modelo
        if (sources.isEmpty()) {
            return new AnswerResponse(NO_INFO_ANSWER, List.of());
        }

        // 2. Armar el contexto etiquetado
        String context = buildContext(sources);

        // 3. Preguntar al modelo con las reglas, el contexto y la pregunta
        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user("CONTEXTO:\n" + context + "\n\nPREGUNTA: " + question)
                .call()
                .content();

        // 3.1 si el modelo no encontro la respuesta, no tiene sentido hacer devoluciones de fuentes.
        if (answer == null || answer.contains(NO_INFO_ANSWER)) {
            return new AnswerResponse(NO_INFO_ANSWER, List.of());
        }

        // 4. Responder con el texto y las fuentes usadas
        return new AnswerResponse(answer, sources);
    }

    /** Une los fragmentos, cada uno con su etiqueta [Título, pág. N]. */
    private String buildContext(List<SearchResult> sources) {
        return sources.stream()
                .map(s -> label(s) + "\n" + s.text())
                .collect(Collectors.joining("\n\n---\n\n"));
    }

    private String label(SearchResult source) {
        return source.page() == null
                ? "[" + source.title() + "]"
                : "[" + source.title() + ", pág. " + source.page() + "]";
    }
}