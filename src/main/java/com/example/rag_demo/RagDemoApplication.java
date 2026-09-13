package com.example.rag_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the RAG (Retrieval-Augmented Generation) Demo.
 * 
 * This application demonstrates the implementation of a RAG pattern using:
 * - Spring AI framework for AI/ML integration
 * - Ollama for local LLM inference (LLaMA 3.1)
 * - SimpleVectorStore for in-memory document embeddings
 * - Spring Boot for REST API endpoints
 * 
 * The application provides intelligent Q&A capabilities over enterprise documents
 * by combining vector similarity search with LLM generation.
 * 
 * Architecture Overview:
 * - DataLoaderService: Loads and vectorizes documents on startup
 * - RagController: Provides REST endpoints for RAG queries
 * - VectorStore: Stores document embeddings for similarity search
 * - ChatClient: Interfaces with Ollama for LLM inference
 * 
 * @author Karem MHAMDIA
 * @version 0.0.1-SNAPSHOT
 * @since 2026
 */
@SpringBootApplication
public class RagDemoApplication {

	/**
	 * Main entry point for the Spring Boot application.
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(RagDemoApplication.class, args);
	}

}
