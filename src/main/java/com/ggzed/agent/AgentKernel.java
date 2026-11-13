package com.ggzed.agent;

import com.ggzed.agent.tools.WeatherTool;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;
import org.springframework.stereotype.Service;

@Service
public class AgentKernel {
    private final ToolRegistry toolRegistry;

    public AgentKernel(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    public WeatherAssistant build(AgentConfig cfg) {
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl(cfg.getBaseUrl()) // 你的 Ollama 地址
                .modelName(cfg.getModelName())                 // 模型名称
                .build();

        // 关键：注册工具实例
        //参数错误直接导致调用失败
        return AiServices.builder(WeatherAssistant.class)
                .chatModel(model)
                .tools(toolRegistry.resolve(cfg.getAllowedToolNames())) // 关键：注册工具实例
                .systemMessageProvider(s->cfg.getSystemPrompt())
                .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.failInvocation())//参数错误直接导致调用失败
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.failInvocation())
                .build();
    }
}
