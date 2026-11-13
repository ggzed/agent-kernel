package com.ggzed.agent;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Component
public class ConfigRepository {
    private static final String CONFIG_FILE = "agents.yml";

    public AgentConfig load(String agentId) {
        Yaml yaml = new Yaml(new Constructor(AgentConfig.class, new LoaderOptions()));
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException(CONFIG_FILE + " not found in classpath");
            }
            for (Object obj : yaml.loadAll(in)) {
                AgentConfig agentConfig = (AgentConfig) obj;
                if (agentId.equals(agentConfig.getAgentId())) {
                    return agentConfig;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        throw new IllegalArgumentException("agent not found: " + agentId);
    }
}
