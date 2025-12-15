package org.example;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Langchain4j_Springboot
{
    public static void main( String[] args )
    {
        SpringApplication.run(Langchain4j_Springboot.class, args);
    }

    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore(
            EmbeddingStore embeddingStore,
            EmbeddingModel embeddingModel){


        return args -> {
            // 2. 添加一些示例数据
            List<TextSegment> documents = List.of(
                    TextSegment.from("hi"),
                    TextSegment.from("你好"),
                    TextSegment.from("hello world"),
                    TextSegment.from("hi there"),
                    TextSegment.from("greetings"),
                    TextSegment.from("good morning"),
                    TextSegment.from("hey you"),
                    TextSegment.from("I like football."),
                    TextSegment.from("我喜欢刘德华")
            );

            // 3. 生成嵌入并存储
            for (TextSegment document : documents) {
                Embedding embedding = embeddingModel.embed(document).content();
                embeddingStore.add(embedding, document);
            }

        };
    }
}
