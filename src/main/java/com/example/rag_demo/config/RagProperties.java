package com.example.rag_demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for RAG (Retrieval-Augmented Generation) settings.
 * 
 * This class binds configuration values from application.yaml under the 'rag' prefix.
 * It allows for externalized configuration of RAG-specific parameters.
 * 
 * @author Karem MHAMDIA
 * @version 0.0.1-SNAPSHOT
 */
@Data
@Component
@ConfigurationProperties(prefix = "rag")
public class RagProperties {

    /**
     * The path to the directory containing documents to be indexed.
     * Supports classpath: prefix for resources or file: for external files.
     * All files in this directory will be loaded and vectorized on startup.
     * 
     * This property is required. Default values are provided in application-dev.yaml.
     */
    private String documentsPath;

    /**
     * Comma-separated list of file extensions to load from the documents directory.
     * Examples: ".txt,.md" or ".pdf,.docx"
     * Only files with these extensions will be processed.
     * 
     * This property is required. Default values are provided in application-dev.yaml.
     */
    private String fileExtensions;

    /**
     * The system prompt template used for RAG queries.
     * This prompt instructs the LLM on how to behave and use the provided context.
     * The {context} placeholder will be replaced with retrieved document chunks.
     */
    private String systemPrompt = """
            You are an enterprise HR assistant. Answer the question based EXCLUSIVELY on the context provided below.
            If the answer cannot be found in the context, politely indicate that you don't have this information.
            
            CONTEXT:
            {context}
            """;

    /**
     * The number of top similar documents to retrieve from the vector store.
     * Higher values provide more context but may increase processing time and noise.
     */
    private int topKDocuments = 2;
}
