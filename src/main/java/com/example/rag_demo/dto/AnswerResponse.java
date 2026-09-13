package com.example.rag_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for answer responses.
 * 
 * This DTO encapsulates the response from the RAG system,
 * providing a structured format for API responses.
 * 
 * @author Karem MHAMDIA
 * @version 0.0.1-SNAPSHOT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {

    /**
     * The generated answer from the LLM based on retrieved context.
     */
    private String answer;

    /**
     * The number of document chunks used to generate the answer.
     */
    private int contextChunksUsed;

    /**
     * Constructor with answer only (context chunks defaults to 0).
     * 
     * @param answer The generated answer
     */
    public AnswerResponse(String answer) {
        this.answer = answer;
        this.contextChunksUsed = 0;
    }
}
