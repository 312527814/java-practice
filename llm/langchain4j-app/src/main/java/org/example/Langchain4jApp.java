package org.example;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.example.tool.Calculator;
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

    @Test
    public void wanxImageModel() {
        String getenv = System.getenv("ALI_AI_KEY");
        WanxImageModel wanxImageModel = WanxImageModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .modelName("wanx2.1-t2i-plus")
                .apiKey(getenv)
                .build();

        Response<Image> response = wanxImageModel.generate("美女");
        System.out.println(response.content().url());
    }
    @Test
    public void QwenChatModel() {
        String getenv = System.getenv("ALI_AI_KEY");
        QwenChatModel qwenChatModel = QwenChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .modelName("qwen3-vl-plus")
                .apiKey(getenv)
                .build();

//        String result = qwenChatModel.chat("请介绍一下你自己，不要超过10个字");
        String result = qwenChatModel.chat("langchain4j是由谁开发出来的？");
        System.out.println(result);
    }
    interface Assistant {

        String chat(String userMessage);
    }

    @Test
    public void Tool() {
        String getenv = System.getenv("ALI_AI_KEY");
        QwenChatModel model = QwenChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .modelName("qwen-max")
                .apiKey(getenv)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(new Calculator())
                .build();

//        String question = "What is the square root of the sum of the numbers of letters in the words \"hello\" and \"world\"?";
//        String question = "请计算3和4的和是多少？";
        String question = "请将'redis'和‘mysql’拼接一下，直接返回结果，不要有多余的补充。";

        String answer = assistant.chat(question);

        System.out.println(answer);
    }
}
