package org.example;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.text.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.text.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.reader.poi.PoiDocumentReader;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.index.CreateIndexParam;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusSearchRequest;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */
/*public class MilvusVectorStoreCrudTest
        extends TestCase {
    *//**
     * Create the test case
     *
     * @param testName name of the test case
     *//*
    public MilvusVectorStoreCrudTest(String testName) {
        super(testName);
    }

    *//**
     * @return the suite of tests being tested
     *//*
    public static Test suite() {
        return new TestSuite(AppDocumentReader.class);
    }

    *//**
     * Rigourous Test :-)
     *//*
    public void testApp() {

        Resource resource = new FileSystemResource("E:\\tmp\\111.pdf");

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());

        List<Document> documents = pdfReader.read();

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(80)           // 每块大约多少 token（越大，Document 越少）
                .withMinChunkSizeChars(350)
                .withMinChunkLengthToEmbed(5)
                .withMaxNumChunks(100)        // 最多切出多少块（硬上限）
                .withKeepSeparator(true)
                .build();
        List<Document> chunks = splitter.apply(documents);

        chunks.forEach(document -> {
            System.out.println("..............");
            System.out.println(document);
        });
    }
}*/

@SpringBootTest(classes = SpringAiAlibabaApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MilvusVectorStoreCrudTest {

    private static final String MILVUS_HOST = "192.168.16.129";
    private static final int MILVUS_PORT = 19530;
    private static final String MILVUS_URI = "http://" + MILVUS_HOST + ":" + MILVUS_PORT;
    private static final String MILVUS_USER = "root";
    private static final String MILVUS_PASSWORD = "milvus";

    private static final String DOC_ID_1 = "doc-001";
    private static final String DOC_ID_2 = "doc-002";
    private static final String DOC_ID_3 = "doc-003";

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private MilvusServiceClient milvusServiceClient;

    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private ReactAgent deepSeekChatAgent;;


    @Bean
    MilvusServiceClient milvusClient() {
        MilvusServiceClient milvusServiceClient = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withUri(MILVUS_URI)
                        .withAuthorization(MILVUS_USER, MILVUS_PASSWORD)
                        .build()
        );

        return milvusServiceClient;
    }


    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = MilvusVectorStoreAutoConfiguration.class)
    static class TestApplication {

        @Bean
        MilvusServiceClient milvusClient() {
            MilvusServiceClient milvusServiceClient = new MilvusServiceClient(
                    ConnectParam.newBuilder()
                            .withUri(MILVUS_URI)
                            .withAuthorization(MILVUS_USER, MILVUS_PASSWORD)
                            .build()
            );

            return milvusServiceClient;
        }

        @Bean
        EmbeddingModel embeddingModel() {
            var dashScopeApi = DashScopeApi.builder()
                    .apiKey(System.getenv("ALI_AI_KEY"))
                    .build();

            var embeddingModel = new DashScopeEmbeddingModel(
                    dashScopeApi,
                    MetadataMode.EMBED,
                    DashScopeEmbeddingOptions.builder()
                            .model(DashScopeModel.EmbeddingModel.EMBEDDING_V4.getValue())
                            .textType(DashScopeModel.EmbeddingTextType.DOCUMENT.getValue())
                            .build(),
                    RetryUtils.DEFAULT_RETRY_TEMPLATE);
            return embeddingModel;
        }

        @Bean
        VectorStore vectorStore(MilvusServiceClient milvusClient, EmbeddingModel embeddingModel) {
            MilvusVectorStore vectorStore = MilvusVectorStore.builder(milvusClient, embeddingModel)
                    .databaseName("spring_ai")
                    .collectionName("test_crud_collection2")
                    .embeddingDimension(embeddingModel.dimensions())
                    .indexType(IndexType.IVF_FLAT)
                    .metricType(MetricType.COSINE)
                    .initializeSchema(false)
                    .build();
            return vectorStore;
        }
    }

    @org.junit.jupiter.api.Test
    @Order(1)
    void init(){
//        milvusServiceClient.createDatabase(
//                CreateDatabaseParam.newBuilder().withDatabaseName("spring_ai").build()
//        );
        FieldType docId = FieldType.newBuilder()
                .withName("doc_id")
                .withDataType(DataType.VarChar)
                .withMaxLength(256)
                .withPrimaryKey(true)
                .withAutoID(false)
                .build();
        FieldType content = FieldType.newBuilder()
                .withName("content")
                .withDataType(DataType.VarChar)
                .withMaxLength(65535)
                .build();
        FieldType metadata = FieldType.newBuilder()
                .withName("metadata")
                .withDataType(DataType.JSON)
                .build();
        FieldType embedding = FieldType.newBuilder()
                .withName("embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(embeddingModel.dimensions())   // 必须和 EmbeddingModel 一致
                .build();
        CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
                .withCollectionName("test_crud_collection2")
                .withDatabaseName("spring_ai")
                .withFieldTypes(List.of(docId, content, metadata, embedding))
                .build();
        milvusServiceClient.createCollection(createParam);


        milvusServiceClient.createIndex(
                CreateIndexParam.newBuilder()
                        .withDatabaseName("spring_ai")
                        .withCollectionName("test_crud_collection2")
                        .withFieldName("embedding")
                        .withIndexType(IndexType.IVF_FLAT)
                        .withMetricType(MetricType.COSINE)
                        .withExtraParam("{\"nlist\":1024}")
                        .build()
        );

    }

    /** 增：插入文档 */
    @org.junit.jupiter.api.Test
    @Order(1)
    void createDocuments() {
        assumeMilvusAvailable();

        List<Document> docs = List.of(
                Document.builder()
                        .id(DOC_ID_1)
                        .text("皮克斯是一家动画公司")
                        .metadata("category", "company")
                        .build(),
                Document.builder()
                        .id(DOC_ID_2)
                        .text("Spring AI 支持 Milvus 向量库")
                        .metadata("category", "tech")
                        .build(),
                Document.builder()
                        .id(DOC_ID_3)
                        .text("Spring AI 支持 Chroma 向量库")
                        .metadata("category", "tech")
                        .build()
        );

        vectorStore.add(docs);
        System.out.println("插入成功: " + docs.size() + " 条");
    }
    /** 增：插入文档 */
    @org.junit.jupiter.api.Test
    @Order(1)
    void createDocuments2() {
        assumeMilvusAvailable();

        Resource resource = new FileSystemResource("E:\\tmp\\111.pdf");

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());

        List<Document> documents = pdfReader.read();

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(500)           // 每块大约多少 token（越大，Document 越少）
                .withMinChunkSizeChars(350)
                .withMinChunkLengthToEmbed(5)
                .withMaxNumChunks(100)        // 最多切出多少块（硬上限）
                .withKeepSeparator(true)
                .build();
        List<Document> chunks = splitter.apply(documents);

        vectorStore.add(chunks);
        System.out.println("插入成功: " + chunks.size() + " 条");
    }

    /** 查：相似度检索 */
    @org.junit.jupiter.api.Test
    @Order(2)
    void queryDocuments() {
        assumeMilvusAvailable();
        // 2. 加载 collection
        milvusServiceClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withDatabaseName("spring_ai")
                        .withCollectionName("test_crud_collection2")
                        .build()
        );
        List<Document> results = vectorStore.similaritySearch(
                MilvusSearchRequest.milvusBuilder()
                        .query("Spring")
                        .topK(2)
                        .similarityThreshold(0.0)
                        .searchParamsJson("{\"nprobe\":16}")
                        .build()
        );

        assertFalse(results.isEmpty());
        results.forEach(doc ->
                System.out.printf("id=%s, score=%.4f, text=%s, metadata=%s%n",
                        doc.getId(), doc.getScore(), doc.getText(), doc.getMetadata())
        );
    }

    @org.junit.jupiter.api.Test
    @Order(2)
    void queryDocuments2() throws GraphRunnerException {
        assumeMilvusAvailable();

        String prompt = "我接下来要做什么？";
        // 2. 加载 collection
        milvusServiceClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withDatabaseName("spring_ai")
                        .withCollectionName("test_crud_collection2")
                        .build()
        );
        List<Document> results = vectorStore.similaritySearch(
                MilvusSearchRequest.milvusBuilder()
                        .query(prompt)
                        .topK(2)
                        .similarityThreshold(0.0)
                        .searchParamsJson("{\"nprobe\":16}")
                        .build()
        );

        // 方式 A：拼进本次 user prompt（不改 Bean）
        String context = results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));
        String userPrompt = """
        【参考资料】
        %s
        
        【用户问题】
        %s
        
        请依据【参考资料】作答。回答问题。
        """.formatted(context, prompt);

        AssistantMessage call = deepSeekChatAgent.call(userPrompt);

        System.out.println(call.getText());
    }

    /** 查：带 metadata 过滤 */
    @org.junit.jupiter.api.Test
    @Order(3)
    void queryWithMetadataFilter() {
        assumeMilvusAvailable();

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("Spring")
                        .topK(5)
                        .filterExpression("category == 'tech'")
                        .build()
        );

        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(d -> "tech".equals(d.getMetadata().get("category"))));
    }

    /** 改：VectorStore 无 update，用 delete + add 模拟 */
    @org.junit.jupiter.api.Test
    @Order(4)
    void updateDocument() {
        assumeMilvusAvailable();

        vectorStore.delete(List.of(DOC_ID_1));

        Document updated = Document.builder()
                .id(DOC_ID_1)
                .text("皮克斯已被迪士尼收购，主营动画电影")
                .metadata("category", "company")
                .metadata("version", 2)
                .build();

        vectorStore.add(List.of(updated));

        List<Document> results = vectorStore.similaritySearch(
                MilvusSearchRequest.milvusBuilder()
                        .query("皮克斯已被迪士尼收购")
                        .topK(1)
                        .searchParamsJson("{\"nprobe\":16}")
                        .build()
        );

        assertFalse(results.isEmpty());
        assertEquals(DOC_ID_1, results.get(0).getId());
        assertTrue(results.get(0).getText().contains("迪士尼"));
    }

    /** 删：按 id 删除 */
    @org.junit.jupiter.api.Test
    @Order(5)
    void deleteDocuments() {
        assumeMilvusAvailable();

        vectorStore.delete(List.of(DOC_ID_1, DOC_ID_2));

        List<Document> results = vectorStore.similaritySearch(
                MilvusSearchRequest.milvusBuilder()
                        .query("皮克斯")
                        .topK(10)
                        .searchParamsJson("{\"nprobe\":16}")
                        .build()
        );

        assertTrue(results.stream().noneMatch(d ->
                DOC_ID_1.equals(d.getId()) || DOC_ID_2.equals(d.getId())
        ));
        System.out.println("删除完成");
    }

    private void assumeMilvusAvailable() {
        Assumptions.assumeTrue(isMilvusUp(),
                "Milvus 未启动，请先运行: docker compose up -d milvus");
    }

    private boolean isMilvusUp() {
        try (Socket socket = new Socket(MILVUS_HOST, MILVUS_PORT)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}


