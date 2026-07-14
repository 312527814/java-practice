package org.example.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/VectorStore")
public class VectorStoreController {

    @Autowired
    private VectorStore vectorStore;

    @GetMapping("/ai/search")
    public List<Document> search(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        // 检索与查询相似的文档
        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(2).build());
        return results;
    }


}