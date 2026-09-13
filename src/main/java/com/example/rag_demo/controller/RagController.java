package com.example.rag_demo.controller;

import com.example.rag_demo.config.RagProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for RAG (Retrieval-Augmented Generation) operations.
 * 
 * This controller provides endpoints for:
 * - Asking questions with context-aware responses using RAG pattern
 * - Streaming responses for real-time user experience
 * 
 * The RAG pattern combines:
 * 1. Vector similarity search to find relevant document chunks
 * 2. LLM generation with retrieved context for accurate answers
 * 
 * @author Karem MHAMDIA
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@RestController
@Validated
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final RagProperties ragProperties;

    /**
     * Constructor with dependency injection.
     * 
     * @param chatClientBuilder Builder for creating the ChatClient
     * @param vectorStore Vector store for document similarity search
     * @param ragProperties RAG configuration properties
     */
    public RagController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, RagProperties ragProperties) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.ragProperties = ragProperties;
        log.info("RagController initialized with ChatClient, VectorStore, and RagProperties");
    }

    /**
     * Endpoint to ask a question and receive a context-aware response.
     * 
     * This endpoint implements the RAG pattern:
     * 1. Performs vector similarity search to find relevant document chunks
     * 2. Extracts text content from retrieved documents
     * 3. Constructs an augmented prompt with context
     * 4. Calls the LLM with the enriched prompt
     * 
     * @param question The user's question (required)
     * @return The LLM's response based on retrieved context
     */
    @GetMapping("/api/rag/ask")
    public String askQuestion(@RequestParam String question) {
        log.info("Received question: {}", question);

        // Step 1: Vector similarity search to find the most relevant documents
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(ragProperties.getTopKDocuments())
                        .build()
        );
        log.debug("Retrieved {} similar documents", similarDocuments.size());

        // Step 2: Extract text content from retrieved documents
        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        // Step 3: Construct the augmented system prompt with context
        String systemPrompt = ragProperties.getSystemPrompt().replace("{context}", context);

        // Step 4: Call the LLM (Ollama / LLaMA 3.1) with context and question
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();
        
        log.info("Generated response for question");
        return response;
    }

    /**
     * Endpoint to ask a question and receive a streamed response.
     * 
     * Similar to {@link #askQuestion(String)} but returns a reactive stream
     * for real-time response generation, improving user experience for
     * longer responses.
     * 
     * @param question The user's question (required)
     * @return A Flux of strings representing the streamed response
     */
    @GetMapping(value = "/api/rag/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamQuestion(@RequestParam String question) {
        log.info("Received streaming question: {}", question);

        // Step 1: Vector similarity search (same as non-streaming endpoint)
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(ragProperties.getTopKDocuments())
                        .build()
        );

        // Step 2: Extract context from retrieved documents
        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        // Step 3: Construct system prompt with context
        String systemPrompt = ragProperties.getSystemPrompt().replace("{context}", context);

        // Step 4: Call the LLM with streaming enabled
        return chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .stream()          // Enables streaming mode
                .content();        // Returns Flux<String> for reactive streaming
    }
}