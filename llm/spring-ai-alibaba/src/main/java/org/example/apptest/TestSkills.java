package org.example.apptest;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.shelltool.ShellToolAgentHook;
import com.alibaba.cloud.ai.graph.agent.hook.skills.SkillsAgentHook;
import com.alibaba.cloud.ai.graph.agent.interceptor.contextediting.ContextEditingInterceptor;
import com.alibaba.cloud.ai.graph.agent.tools.ShellTool2;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.filesystem.FileSystemSkillRegistry;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class TestSkills {
    public static void main(String[] args) throws Exception {
        // 初始化 ChatModel
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .build();

        ChatModel chatModel = DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(DeepSeekChatOptions.builder()
                        .model("deepseek-v4-pro")
                        .temperature(0.7)
                        .build())
                .build();

        String skillsDirectory = getTestSkillsDirectory();

        // 1. 技能注册表：从 src/test/resources/skills 加载
        SkillRegistry registry = FileSystemSkillRegistry.builder()
                .projectSkillsDirectory(skillsDirectory)
                .build();

        // 2. Skills Hook：注册 read_skill 工具并注入技能列表到系统提示
        SkillsAgentHook skillsHook = SkillsAgentHook.builder()
                .skillRegistry(registry)
                .build();

        // 3. Shell Hook：工作目录设为技能根目录，便于执行 scripts/extract_pdf.py
        ShellToolAgentHook shellHook = ShellToolAgentHook.builder()
                .shellTool2(ShellTool2.builder(skillsDirectory + "/skills/pdf-extractor").build())
                .build();

        ContextEditingInterceptor build1 = ContextEditingInterceptor.builder().trigger(120000).clearAtLeast(60000).build();

        // 4. 构建 Agent
        ReactAgent agent = ReactAgent.builder()
                .name("skills-integration-agent")
                .model(chatModel)
                .saver(new MemorySaver())
                .interceptors(build1)
                .hooks(List.of(skillsHook, shellHook))
                .enableLogging(true)
                .build();

        // 5. 使用真实 PDF 路径（与官方 AgentSkillsTest 一致）
        String pdfPath = skillsDirectory + "/skills/pdf-extractor/saa-roadmap.pdf";
        AssistantMessage response = agent.call("请从 " + pdfPath + " 文件中提取关键信息。");

        System.out.println("回复内容：");
        System.out.println(response.getText());
    }

    private static String getTestSkillsDirectory() throws Exception {
        Resource resource = new ClassPathResource("skills");
        if (resource.exists() && resource.getFile().isDirectory()) {
            return resource.getFile().getAbsolutePath();
        }
        URL url = TestSkills.class.getClassLoader().getResource("skills");
        if (url != null && "file".equals(url.getProtocol())) {
            return Path.of(url.toURI()).toString();
        }
        throw new IllegalStateException("Cannot find test skills directory under src/test/resources/skills");
    }
}
