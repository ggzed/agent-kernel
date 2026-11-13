# PROGRESS.md — agent-kernel 主干进度锚点
> 规则：决策日志只追加不修改；状态区可覆盖更新。
> 用法：每次回主干对话，先贴本文件，再说"我做了什么 / 卡在哪"。

## 一、决策日志（不可变）
- D0 仓库策略：旧仓库降级为"博物馆"（保留 feature 分支），新建单模块 agent-kernel 实验田
- D0 技术选型：LangChain4j 1.22.0（理由：控制感，零件可单独拿在手里；不用 Spring AI 因抽象层厚、约定优先）
- D0 架构：单模块，不拆微服务、不上 Nacos / Gateway / admin
- D0 冻结范围：前端17页 / admin计费审计 / RAG / 多Agent编排 / 知识库CRUD
- D0 核心目标：把 AgentKernel 从"模拟配置"变成"真实配置驱动 + 有边界 + 可评估"
- D0 验收标准：改DB/YAML里的systemPrompt → 不重启 → refreshAgent() → 同sessionId下一轮行为变化 → Evals通过率不掉
- D0 工作分工：白天纯内网做设计（Evals用例、配置模型、对照表）；晚上连外网做实现（调API、跑测试）
- D1 代码来源规则：样板代码（依赖/配置/DTO）可用生成；核心逻辑（ReAct循环/保险丝/配置热更新/Evals断言）必须手写并通过"关屏默写"检验
- D1 红线：代码中禁止出现 ip:port / 模型名 / key 字面量，一律进 application.yml；空 class 不计入进度
- D1 补充红线：配置外置 ≠ 配置驱动。把 IP 搬进 yml 只是"外置"；只有当 systemPrompt / maxRounds / allowedTools 也从配置读出来并真正影响行为，才叫"驱动"，才是 D2 的验收。
- 
## 二、14天计划
- D1 建仓库 + 跑通官方天气示例
- D2 干掉 getConfig() 的 if-else（ConfigRepository 替代）  ← 当前位置
- D3 refreshAgent() 热更新
- D4 保险丝：maxSequentialToolInvocations(5)
- D5 重复调用检测 + 工具返回值引导
- D6 工具异常收口（返回短句而非堆栈）
- D7 超时：OpenAiChatModel.timeout(30s)
- D8-D10 EvalTest.java：30条用例，跑通过率
- D11-D12 日志：每轮记录工具名/耗时/TokenUsage
- D13 手写 vs 框架对照表（面试资产）
- D14 README：只讲一件事——这个内核解决什么问题

## 三、当前状态
- 已完成：D1 正式关账（工具调用证据已入自检记录）；D2 骨架落地（AgentKernel.build / ConfigRepository.load / ToolRegistry 注册与过滤，均有真实逻辑）；groupId 统一、pom 死属性清理、@P 补描述、agents.yml 落地
- 进行中：D2 收尾（未验收） —— systemPrompt 未接入模型，改配置不改行为，主验收不成立
- D2 验收必做：① AiServices.systemMessageProvider(id -> cfg.getSystemPrompt()) 接上，验证"改 systemPrompt 行为变化"；② 删孤儿 OllamaConfig + application.yml 的 model.ollama 段；③ maxRounds→Integer、temperature→Double 并真正传给 builder
- 待清理：README UTF-16→UTF-8（连续两轮未动，第二次实证）；确认 application-local.yml 移出后本地连接来源
- 已知雷（D6 不动）：failInvocation() 异常直穿 HTTP 层
- 下一步：D2 验收通过后，D3 解决"每次请求 new model/AiServices"——这个写法物理上塞不进 ChatMemory，"同 sessionId 下一轮行为变化"无从谈起
- 阻塞点：无

## 四、自检记录
（每5-7轮填一次：复述"目标/冻结范围/验收标准"是否一致）
工具是否真实调用
2026-11-14T00:49:58.245+08:00  INFO 4140 --- [nio-8080-exec-2] com.ggzed.agent.tools.WeatherTool        : [TOOL CALLED] 成都