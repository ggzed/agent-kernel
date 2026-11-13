package com.ggzed.agent;

import com.ggzed.agent.model.OllamaConfig;
import com.ggzed.agent.tools.WeatherTool;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/agent")
public class AgentController {
    private OllamaConfig ollamaConfig;

    public AgentController(OllamaConfig ollamaConfig) {this.ollamaConfig = ollamaConfig;}
    @GetMapping("/chat")
    public String chat(@RequestParam String city) {

        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl(ollamaConfig.getBaseUrl()) // 你的 Ollama 地址
                .modelName(ollamaConfig.getName())                 // 模型名称
                .build();

        WeatherAssistant weatherAssistant1 = AiServices.builder(WeatherAssistant.class)
                .chatModel(model)
                .tools(new WeatherTool()) // 关键：注册工具实例
                .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.failInvocation())//参数错误直接导致调用失败
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.failInvocation())
                .build();
        return weatherAssistant1.chat(city);
    }
}
