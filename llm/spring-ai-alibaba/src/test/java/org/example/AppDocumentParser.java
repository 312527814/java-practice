package org.example;

import com.alibaba.cloud.ai.document.DocumentParser;
import com.alibaba.cloud.ai.parser.tika.TikaDocumentParser;
import com.alibaba.cloud.ai.reader.poi.PoiDocumentReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppDocumentParser
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppDocumentParser(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppDocumentParser.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp1() throws IOException {
        // 创建 Tika Parser
        DocumentParser parser = new TikaDocumentParser();

        // 读取文件输入流
        Resource resource = new FileSystemResource("E:\\tmp\\111.pdf");
        try (InputStream inputStream = resource.getInputStream()) {
            // 解析文档
            List<Document> documents = parser.parse(inputStream);
            documents.forEach(document -> {
                System.out.println("..............");
                System.out.println(document);
            });
        }
    }


    public void testApp() throws IOException {

        // 读取文件输入流
        Resource resource = new FileSystemResource("E:\\tmp\\111.pdf");
        try (InputStream inputStream = resource.getInputStream()) {

            // 创建自定义的文本格式化器
            ExtractedTextFormatter formatter = ExtractedTextFormatter.builder()
                    .withNumberOfTopTextLinesToDelete(0)
                    .withNumberOfBottomTextLinesToDelete(0)
                    .build();

            // 使用格式化器创建 Parser
            TikaDocumentParser parser = new TikaDocumentParser(formatter);

            List<Document> parse = parser.parse(inputStream);
            parse.forEach(document -> {
                System.out.println("..............");
                System.out.println(document);
            });
        }
    }
}
