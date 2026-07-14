package org.example;

import com.alibaba.cloud.ai.reader.poi.PoiDocumentReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppDocumentReader
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppDocumentReader(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppDocumentReader.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        // 使用文件路径创建 Reader
        DocumentReader reader = new PoiDocumentReader(new FileSystemResource("D:\\tanzer\\业务梳理.docx"));

        // 读取文档内容
        List<Document> documents = reader.get();

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
}
