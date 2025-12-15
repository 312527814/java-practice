package com.my;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class Langchain4jApp {
    public static void main(String[] args) {
        deepseek();
    }


    public static void deepseek() {

        ChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")
                .apiKey(System.getenv("deepseek.api.key"))
                .modelName("deepseek-reasoner")
                .returnThinking(false)
                .build();
        InMemoryChatMemoryStore store = new InMemoryChatMemoryStore();
        String memoryId="foo";
        List<ChatMessage> messages = store.getMessages(memoryId);
        messages.add(new UserMessage("请记住我叫刘德华，回答不要超过10个字"));
        store.updateMessages(memoryId,messages);

        ChatResponse chat = model.chat(store.getMessages(memoryId));
        messages.add(chat.aiMessage());

        store.updateMessages(memoryId,messages);

        messages.add(new UserMessage("我叫什么名字？回答不要超过10个字"));
        store.updateMessages(memoryId,store.getMessages(memoryId));
        System.out.println(store.getMessages(memoryId));

        System.out.println("....................");
        ChatResponse chat2 = model.chat(store.getMessages(memoryId));
        System.out.println(chat2);



    }

    @Test
    public  void ollama() {

        ChatModel model = OllamaChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")
                .temperature(0.0)
                .logRequests(true)
                .logResponses(true)
                .modelName("TINY_DOLPHIN_MODEL")
                .build();
        String result = model.chat("介绍一下你自己,不要超过10个字");
        System.out.println(result);
    }
    @Test
    public void MemoryStore() {
        InMemoryChatMemoryStore store = new InMemoryChatMemoryStore();
        store.updateMessages("foo", Arrays.asList(new UserMessage("abc def"), new AiMessage("ghi jkl")));

        List<ChatMessage> foo = store.getMessages("foo");
        System.out.println(foo);
        store.deleteMessages("foo");
        System.out.println(foo);

    }
}
