package com.example.rag_demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller for basic LLM functionality verification.
 * 
 * This controller provides a simple endpoint to test the LLM connection
 * without RAG context. It's useful for:
 * - Verifying Ollama connectivity
 * - Testing model responses
 * - Debugging LLM configuration issues
 * 
 * @author Karem MHAMDIA
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@RestController
@Validated
public class TestController {

    private final ChatClient chatClient;
    private static final String DEFAULT_MESSAGE = "Bonjour, présente-toi brièvement.";

    /**
     * Constructor with dependency injection.
     * 
     * @param chatClientBuilder Builder for creating the ChatClient
     */
    public TestController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        log.info("TestController initialized");
    }

    /**
     * Test endpoint to verify LLM functionality.
     * 
     * This endpoint sends a direct message to the LLM without any
     * RAG context, useful for testing basic connectivity and model responses.
     * 
     * @param message The message to send to the LLM (defaults to a greeting)
     * @return The LLM's response
     */
    @GetMapping("/api/test")
    public String generate(@RequestParam(defaultValue = DEFAULT_MESSAGE) String message) {
        log.debug("Test request received with message: {}", message);
        
        String response = this.chatClient.prompt()
                .user(message)
                .call()
                .content();
        
        log.debug("Test response generated");
        return response;
    }
}