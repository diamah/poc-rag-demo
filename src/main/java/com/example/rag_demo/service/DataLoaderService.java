package com.example.rag_demo.service;

import com.example.rag_demo.config.RagProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for loading and processing documents into the vector store.
 * 
 * This service handles the initial data ingestion phase of the RAG pipeline:
 * 1. Loading documents from a configured directory
 * 2. Splitting documents into manageable chunks for better retrieval
 * 3. Generating embeddings and storing them in the vector database
 * 
 * The service runs automatically on application startup via @PostConstruct.
 * 
 * @author Karem MHAMDIA
 * @version 0.0.1-SNAPSHOT
 */
@Slf4j
@Service
public class DataLoaderService {

    private final VectorStore vectorStore;
    private final RagProperties ragProperties;
    private final PathMatchingResourcePatternResolver resourceResolver;

    /**
     * Constructor with dependency injection.
     * 
     * @param vectorStore The vector store for storing document embeddings
     * @param ragProperties RAG configuration properties
     * @param resourceResolver Resource resolver for loading files from directory
     */
    public DataLoaderService(VectorStore vectorStore, RagProperties ragProperties) {
        this.vectorStore = vectorStore;
        this.ragProperties = ragProperties;
        this.resourceResolver = new PathMatchingResourcePatternResolver();
    }

    /**
     * Initializes the vector store with document data on application startup.
     * 
     * This method executes the RAG ingestion pipeline:
     * 1. Loads all text documents from the configured directory
     * 2. Splits documents into chunks using token-based splitting
     * 3. Generates embeddings for each chunk and stores them in the vector database
     * 
     * The chunking strategy is crucial for effective retrieval:
     * - Smaller chunks provide more granular search results
     * - Token-based splitting ensures chunks don't break in the middle of words
     * - The default TokenTextSplitter uses sensible defaults for most use cases
     */
    @PostConstruct
    public void init() {
        log.info("Starting document ingestion process from path: {}", ragProperties.getDocumentsPath());

        try {
            // Step 1: Load all text documents from the configured directory
            List<Document> allDocuments = loadDocumentsFromDirectory();
            
            if (allDocuments.isEmpty()) {
                log.warn("No documents found in path: {}", ragProperties.getDocumentsPath());
                return;
            }
            
            log.info("Loaded {} document(s) from directory", allDocuments.size());

            // Step 2: Split documents into chunks for better retrieval
            // TokenTextSplitter breaks documents into smaller pieces based on token count
            // This improves the precision of vector similarity search
            TokenTextSplitter tokenSplitter = TokenTextSplitter.builder().build();
            List<Document> splitDocuments = tokenSplitter.apply(allDocuments);
            log.info("Split documents into {} chunks", splitDocuments.size());

            // Step 3: Generate embeddings and store in vector database
            // The vector store will use the configured embedding model to convert
            // text chunks into vector representations for similarity search
            this.vectorStore.add(splitDocuments);
            log.info("Successfully loaded and vectorized documents in VectorStore");

        } catch (Exception e) {
            log.error("Failed to load documents into vector store", e);
            throw new RuntimeException("Document ingestion failed", e);
        }
    }

    /**
     * Loads all documents from the configured directory path.
     * 
     * Supports both classpath: and file: prefixes:
     * - classpath:docs/ - loads from src/main/resources/docs/
     * - file:/path/to/docs/ - loads from external directory
     * 
     * Only loads files with extensions specified in rag.file-extensions configuration.
     * 
     * @return List of Document objects loaded from all files in the directory
     * @throws IOException If there's an error reading the files
     */
    private List<Document> loadDocumentsFromDirectory() throws IOException {
        List<Document> documents = new ArrayList<>();
        String path = ragProperties.getDocumentsPath();
        String[] extensions = ragProperties.getFileExtensions().split(",");
        
        // Handle classpath vs file paths differently
        String basePath = path.startsWith("classpath:") ? path.replace("classpath:", "classpath*:") : path;
        
        // Load files for each configured extension
        for (String extension : extensions) {
            extension = extension.trim();
            String locationPattern = basePath.endsWith("/") ? basePath + "*" + extension : basePath + "/*" + extension;
            
            log.debug("Loading documents with pattern: {}", locationPattern);
            
            try {
                Resource[] resources = resourceResolver.getResources(locationPattern);
                
                for (Resource resource : resources) {
                    try {
                        TextReader textReader = new TextReader(resource);
                        List<Document> fileDocuments = textReader.get();
                        documents.addAll(fileDocuments);
                        log.debug("Loaded {} documents from file: {}", fileDocuments.size(), resource.getFilename());
                    } catch (Exception e) {
                        log.warn("Failed to load file: {}, skipping...", resource.getFilename(), e);
                    }
                }
            } catch (IOException e) {
                log.debug("No files found for extension: {}", extension);
            }
        }
        
        return documents;
    }
}