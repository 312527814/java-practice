package org.example.embedding;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.Test;

import java.util.List;

public class EmbeddingApp {
    private QwenEmbeddingModel getModel(String modelName) {
        String key = System.getenv("ALI_AI_KEY");
        return QwenEmbeddingModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .apiKey(key)
                .modelName(modelName)
                .build();
    }
    @Test
    public void should_embed_one_text() {
        EmbeddingModel model = getModel("text-embedding-v4");
        TextSegment segment1 = TextSegment.from("hello");
        Response<Embedding> hello = model.embed(segment1);
        Embedding embedding = hello.content();
        float[] vector = embedding.vector();

        for (float v : vector) {
            System.out.println(v);
        }

    }
    @Test
    public void should_embed_one_text_Store() {

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingModel embeddingModel = getModel("text-embedding-v4");

        TextSegment segment1 = TextSegment.from("I like football.");
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("The weather is good today.");
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);

        Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)
                .build();
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(embeddingSearchRequest).matches();
        EmbeddingMatch<TextSegment> embeddingMatch = matches.get(0);

        System.out.println(embeddingMatch.score()); // 0.8144288515898701
        System.out.println(embeddingMatch.embedded().text()); // I like football.



    }

    @Test
    public  void should_embed_one_text_Store2() {
        // 1. 初始化模型和存储
        EmbeddingModel embeddingModel =getModel("text-embedding-v4");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 2. 添加一些示例数据
        List<TextSegment> documents = List.of(
                TextSegment.from("hi"),
                TextSegment.from("你好"),
                TextSegment.from("hello world"),
                TextSegment.from("hi there"),
                TextSegment.from("greetings"),
                TextSegment.from("good morning"),
                TextSegment.from("hey you"),
                TextSegment.from("I like football.")
        );

        // 3. 生成嵌入并存储
        for (TextSegment document : documents) {
            Embedding embedding = embeddingModel.embed(document).content();
            embeddingStore.add(embedding, document);
        }

        // 4. 查询与 "hi" 相似的句子 - 正确API
        String query = "hi";
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        // 方法1：使用 search() 方法（推荐）
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)  // 返回最相似的3个
                .minScore(0.5)  // 最小相似度阈值（可选）
                .build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);

        // 方法2：如果你想要更简单的方式，可以使用重载的 search() 方法
        // EmbeddingSearchResult<TextSegment> result = embeddingStore.search(
        //     queryEmbedding,  // 查询嵌入
        //     3,               // 最大结果数
        //     0.5              // 最小相似度（可选）
        // );

        // 5. 处理结果
        System.out.println("查询: \"" + query + "\"");
        System.out.println("相似句子:");

        for (EmbeddingMatch<TextSegment> match : result.matches()) {
            System.out.printf("  - %s (相似度: %.4f)%n",
                    match.embedded().text(),
                    match.score());
        }
    }
}
