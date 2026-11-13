package com.ggzed.agent.tools;

import com.ggzed.agent.ToolRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class InitTools {

    private final ToolRegistry toolRegistry;

    public InitTools(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    @PostConstruct
    public void init() {
        // 这里注册工具
        toolRegistry.toolRegistry(new WeatherTool());
    }
}