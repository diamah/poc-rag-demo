package com.example.rag_demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RAG (Retrieval-Augmented Generation) components.
 * 
 * This class configures the vector store used for storing and retrieving
 * document embeddings. The SimpleVectorStore is an in-memory implementation
 * suitable for development and POC purposes.
 * 
 * For production environments, consider using:
 * - PostgreSQL with pgvector extension
 * - Pinecone
 * - Weaviate
 * - Milvus
 * 
 * @author Karem MHAMDIA
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@Configuration
public class RagConfig {

    /**
     * Configures a SimpleVectorStore bean for document embeddings.
     * 
     * The vector store is responsible for:
     * - Storing document embeddings (vector representations of text)
     * - Performing similarity search to find relevant documents
     * - Managing the indexing and retrieval of vector data
     * 
     * @param embeddingModel The embedding model used to convert text to vectors
     * @return Configured VectorStore instance
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        log.info("Initializing SimpleVectorStore with embedding model");
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
