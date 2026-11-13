package com.ggzed.agent;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ToolRegistry {
    private final Map<String, Object> toolsByName = new ConcurrentHashMap<>();

    /**
     * 扫描对象里所有 @Tool 方法，按方法名注册
     *
     * @param toolObject
     */
    public void toolRegistry(Object toolObject) {
        for (Method m : toolObject.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Tool.class)) {
                toolsByName.put(m.getName(), toolObject);
            }
        }
    }

    /**
     * 过滤出允许的工具对象，直接传给 .tools(Object...)
     */
    public Object[] resolve(Set<String> allowTools) {
        if (allowTools == null || allowTools.isEmpty()) {
            return new Object[0];
        }
        return toolsByName.entrySet().stream()
                .filter(entry -> allowTools.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .distinct()
                .toArray();
    }
}
