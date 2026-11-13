package com.ggzed.agent.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "model.ollama")
public class OllamaConfig {
    private String baseUrl;
    private String name;
}
