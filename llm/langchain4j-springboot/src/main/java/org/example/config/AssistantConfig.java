package org.example.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfig {
    @Bean
    public OllamaChatModel ollamaChatModel(){
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.1")
                .build();
        return model;
    }
    @Bean
    public QwenChatModel qwenChatModel(){
        String getenv = System.getenv("ALI_AI_KEY");
        QwenChatModel model = QwenChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .modelName("qwen-max")
                .apiKey(getenv)
                .build();
        return model;
    }

    @Bean // 为Bean命名
    public ChatMemoryProvider chatMemoryProvider() {
        return new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object o) {
                // 创建一个可记住最近10轮对话的内存
                return MessageWindowChatMemory.withMaxMessages(10);
            }
        };
    }
    @Bean // 为Bean命名
    public ContentRetriever contentRetriever(EmbeddingStore embeddingStore, EmbeddingModel embeddingModel) {
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.75)
                .build();
        return contentRetriever;
    }

    @Bean
    public EmbeddingStore embeddingStore(){
        return new InMemoryEmbeddingStore<>();
    }
    @Bean
    public QwenEmbeddingModel embeddingModel(){
        String key = System.getenv("ALI_AI_KEY");
        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .apiKey(key)
                .modelName("text-embedding-v4")
                .build();
        return  embeddingModel;
    }
}
