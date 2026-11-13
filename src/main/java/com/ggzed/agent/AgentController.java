package com.ggzed.agent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
public class AgentController {
    private final AgentKernel kernel;
    private final ConfigRepository repo;

    public AgentController(AgentKernel kernel, ConfigRepository repo) {
        this.kernel = kernel;
        this.repo = repo;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String agentId, @RequestParam String msg) {
        return kernel
                .build(repo.load(agentId))
                .chat(msg);
    }
}
