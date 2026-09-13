package com.example.rag_demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for question requests.
 * 
 * This DTO encapsulates the question parameter sent to the RAG endpoints.
 * Using a DTO provides better type safety and validation compared to
 * using raw @RequestParam strings.
 * 
 * @author Karem MHAMDIA
 * @version 0.0.1-SNAPSHOT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    /**
     * The question to be answered by the RAG system.
     * Must not be blank or empty.
     */
    @NotBlank(message = "Question cannot be blank")
    private String question;
}
