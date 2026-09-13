# RAG Demo - Retrieval-Augmented Generation with Spring AI

A production-ready demonstration of the RAG (Retrieval-Augmented Generation) pattern implemented with Spring Boot, Spring AI, and Ollama. This application showcases intelligent document Q&A capabilities by combining vector similarity search with Large Language Model (LLM) generation.

## 🎯 Overview

This project implements a complete RAG pipeline for answering questions based on enterprise documents. The system:

- **Ingests** documents and splits them into manageable chunks
- **Vectorizes** text chunks using embedding models
- **Stores** embeddings in a vector database for similarity search
- **Retrieves** relevant document chunks based on user queries
- **Generates** context-aware responses using LLaMA 3.1

## 🏗️ Architecture

```
┌─────────────────┐
│  User Question  │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│                    RagController                         │
│  ┌───────────────────────────────────────────────────┐  │
│  │  1. Vector Similarity Search (VectorStore)        │  │
│  │  2. Context Extraction                           │  │
│  │  3. Prompt Augmentation                           │  │
│  │  4. LLM Generation (ChatClient)                   │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│              VectorStore (SimpleVectorStore)            │
│  - Stores document embeddings                           │
│  - Performs similarity search                          │
│  - In-memory implementation (POC)                      │
└─────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│              DataLoaderService                          │
│  - Loads documents from resources                       │
│  - Splits documents into chunks                          │
│  - Generates embeddings via EmbeddingModel             │
└─────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│                    Ollama (Local)                       │
│  - LLaMA 3.1 (8B) for chat completion                  │
│  - Nomic Embed Text for embeddings                      │
└─────────────────────────────────────────────────────────┘
```

## 🚀 Features

- **Intelligent Document Q&A**: Ask questions and receive context-aware answers
- **Streaming Responses**: Real-time response generation for better UX
- **Vector Similarity Search**: Efficient retrieval of relevant document chunks
- **Automatic Document Ingestion**: Documents are loaded and vectorized on startup
- **RESTful API**: Clean REST endpoints for integration
- **Health Monitoring**: Spring Boot Actuator for production readiness
- **Comprehensive Logging**: Structured logging for debugging and monitoring
- **Exception Handling**: Global exception handling with consistent error responses

## 🛠️ Tech Stack

- **Java 21** - Modern Java with records and pattern matching
- **Spring Boot 4.1.1** - Application framework
- **Spring AI 2.0.1** - AI/ML integration framework
- **Ollama** - Local LLM inference (LLaMA 3.1, Nomic Embed Text)
- **Maven** - Dependency management and build tool
- **Lombok** - Reduce boilerplate code
- **SimpleVectorStore** - In-memory vector database (POC)

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 21** or higher installed
- **Maven 3.8+** for building the project
- **Ollama** installed and running on `localhost:11434`

### Installing Ollama

```bash
# Install Ollama (Linux/Mac)
curl -fsSL https://ollama.com/install.sh | sh

# Pull required models
ollama pull llama3.1:8b
ollama pull nomic-embed-text

# Start Ollama server
ollama serve
```

## 🔧 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/rag-demo.git
   cd rag-demo
   ```

2. **Build the project**
   ```bash
   ./mvnw clean install
   ```

3. **Configure Ollama**
   Ensure Ollama is running:
   ```bash
   ollama serve
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

## 📡 API Endpoints

### 1. Ask Question (Non-streaming)
```http
GET /api/rag/ask?question=Your question here
```

**Example:**
```bash
curl "http://localhost:8080/api/rag/ask?question=Quels sont les droits de télétravail?"
```

**Response:**
```json
"Les collaborateurs en CDI ont droit à 2 jours de télétravail par semaine (mardi et jeudi)..."
```

### 2. Ask Question (Streaming)
```http
GET /api/rag/stream?question=Your question here
```

**Example:**
```bash
curl -N "http://localhost:8080/api/rag/stream?question=Quel est le budget formation?"
```

**Response:** Server-Sent Events (SSE) stream

### 3. Test LLM Connection
```http
GET /api/test?message=Your message
```

**Example:**
```bash
curl "http://localhost:8080/api/test?message=Hello, introduce yourself"
```

### 4. Health Check
```http
GET /actuator/health
```

## 📁 Project Structure

```
rag-demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/rag_demo/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── RagConfig.java   # Vector store configuration
│   │   │   │   └── WebConfig.java   # CORS and web configuration
│   │   │   ├── controller/          # REST controllers
│   │   │   │   ├── RagController.java    # RAG endpoints
│   │   │   │   └── TestController.java   # LLM test endpoint
│   │   │   ├── service/             # Business logic
│   │   │   │   └── DataLoaderService.java # Document ingestion
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── QuestionRequest.java
│   │   │   │   └── AnswerResponse.java
│   │   │   ├── exception/           # Exception handling
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── RagDemoApplication.java # Main application class
│   │   └── resources/
│   │       ├── application.yaml     # Application configuration
│   │       └── docs/                # Document resources
│   │           └── charte_entreprise.txt
│   └── test/                        # Test classes
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## 🔍 How RAG Works

### The RAG Pipeline

1. **Document Ingestion** (Startup)
   - Documents are loaded from `src/main/resources/docs/`
   - Text is split into chunks using `TokenTextSplitter`
   - Each chunk is converted to an embedding vector
   - Vectors are stored in the `VectorStore`

2. **Query Processing** (Runtime)
   - User submits a question via REST API
   - Question is converted to an embedding vector
   - VectorStore performs similarity search (top-K retrieval)
   - Most relevant document chunks are retrieved

3. **Response Generation**
   - Retrieved chunks are combined into context
   - System prompt is augmented with context
   - LLM generates response based on context and question
   - Response is returned to user (streaming or non-streaming)

### Key Components

- **EmbeddingModel**: Converts text to vector representations
- **VectorStore**: Stores and retrieves embeddings via similarity search
- **ChatClient**: Interfaces with Ollama for LLM inference
- **TokenTextSplitter**: Splits documents into optimal chunk sizes

## ⚙️ Configuration

### Application Configuration (`application.yaml`)

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3.1:8b
      embedding:
        options:
          model: nomic-embed-text
```

### Customization

- **Change Models**: Modify the model names in `application.yaml`
- **Adjust Top-K**: Change `TOP_K_DOCUMENTS` constant in `RagController`
- **Add Documents**: Place new documents in `src/main/resources/docs/`
- **Logging Levels**: Adjust logging in `application.yaml`

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

### Manual Testing

```bash
# Test basic LLM connection
curl "http://localhost:8080/api/test"

# Test RAG with streaming
curl -N "http://localhost:8080/api/rag/stream?question=Quels sont les congés payés?"

# Test RAG without streaming
curl "http://localhost:8080/api/rag/ask?question=Quelle est la prime d'équipement?"

# Check health
curl "http://localhost:8080/actuator/health"
```

## 🚀 Production Considerations

This is a POC/ demonstration project. For production deployment, consider:

### Vector Store
- Replace `SimpleVectorStore` with a persistent solution:
  - **PostgreSQL + pgvector**: Open-source, SQL-based
  - **Pinecone**: Managed vector database
  - **Weaviate**: Open-source, AI-native
  - **Milvus**: Open-source, scalable

### Security
- Add authentication/authorization (Spring Security)
- Implement rate limiting
- Validate and sanitize all inputs
- Use HTTPS in production
- Restrict CORS origins

### Scalability
- Implement caching for frequent queries
- Add connection pooling for Ollama
- Consider async processing for document ingestion
- Implement batch processing for large document sets

### Monitoring
- Configure structured logging (ELK stack, Loki)
- Add metrics (Prometheus, Grafana)
- Implement distributed tracing (Zipkin, Jaeger)
- Set up alerting for failures

### LLM Optimization
- Fine-tune models for specific domains
- Implement prompt engineering best practices
- Add response validation and fact-checking
- Consider model quantization for faster inference

## 📚 Key Concepts

### Retrieval-Augmented Generation (RAG)
RAG combines retrieval-based and generation-based approaches:
- **Retrieval**: Finds relevant information from a knowledge base
- **Generation**: Uses LLM to generate coherent responses
- **Augmentation**: Enhances LLM prompts with retrieved context

### Vector Embeddings
- Text is converted to numerical vectors (embeddings)
- Similar concepts have similar vector representations
- Enables semantic search beyond keyword matching

### Chunking Strategy
- Documents are split into smaller pieces for better retrieval
- Token-based splitting ensures coherent chunks
- Optimal chunk size depends on use case (typically 500-1000 tokens)

### Similarity Search
- Uses cosine similarity or other distance metrics
- Returns top-K most similar document chunks
- Provides context for LLM generation

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

**Karem MHAMDIA** - [LinkedIn](https://linkedin.com/in/yourusername) - [GitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- [Spring AI](https://spring.io/projects/spring-ai) - AI integration framework
- [Ollama](https://ollama.com) - Local LLM inference
- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework

## 📞 Support

For questions or support:
- Open an issue on GitHub
- Contact: your.email@example.com

---

**Built with ❤️ using Spring AI and Ollama**
