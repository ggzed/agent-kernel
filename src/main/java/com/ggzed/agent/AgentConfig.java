package com.ggzed.agent;

import lombok.Data;

import java.util.Set;

@Data
public class AgentConfig {
    private String agentId;
    private String systemPrompt;
    private String modelName;
    private String baseUrl;
    private String temperature;
    private String maxRounds;
    private Set<String> allowedToolNames;
}
