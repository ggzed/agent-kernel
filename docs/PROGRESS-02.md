# PROGRESS.md — agent-kernel 主干进度锚点（PROGRESS-02）
> 规则：决策日志只追加不修改；状态区可覆盖更新。
> 用法：每次回主干对话，先贴本文件，再说"我做了什么 / 卡在哪"。
> 本版基于 2026-11-14 对远端 ggzed/agent-kernel 的逐端点核验重写。上一版 PROGRESS-01 的"当前状态 / 自检记录"区被本版覆盖；决策日志逐条继承并追加。

---

## 〇、本版置顶警告：远端 HEAD 不可信（先读这条）

本轮对同一仓库做只读核验，两个 GitHub API 端点返回了**互相矛盾**的快照：

| 端点 | 报告的最新 commit | 关键文件状态 |
|---|---|---|
| `git/trees/master?recursive=1` | tree sha = **25b6178** | 无 `model/OllamaConfig.java`；`application.yml` = **0 字节**；`AgentKernel.java` = 1381B；`EvalTest.java` = 26B（空壳） |
| `commits/master` + `commits/c920831` | 最新 commit = **c920831**（Nov 13） | **新增** `model/OllamaConfig.java`；`application.yml` = 4 行且**含真实 IP `192.168.101.108`**；`ConfigRepository` 是空类 |

两份快照方向完全相反（一份"孤儿类已删、yml 已清空"，一份"孤儿类刚加、yml 带真实 IP"）。

**结论（按红线判，不猜）**：
- 远端 master 仍处于**被反复 force-push / 历史重写**的不稳定状态，或经镜像代理（ghfast.top）读取到了**缓存不一致**的快照。两种情况都意味着：**当前"远端 HEAD 是哪个 commit"不可作为验收依据**。
- 因此本文件所有"已落地"判定，一律锚定到**上一轮已逐行核验过的 25b6178 快照**，并在每条后标注"（待重测）"。c920831 快照因含真实 IP、与已确认决策相悖，**不计入进度**，仅作"远端不稳定"的证据留档。
- **回主干第一件事不是写代码，是先把 HEAD 钉死**（见第四节 P0）。HEAD 没钉死之前，任何"我改了 X"的叙述都无法被验证——这正是过去连续多轮反复踩的坑。

---

## 一、决策日志（只追加，不可修改）

### 继承自 PROGRESS-01（原文保留，未改动）
- D0 仓库策略：旧仓库降级为"博物馆"（保留 feature 分支），新建单模块 agent-kernel 实验田
- D0 技术选型：LangChain4j 1.22.0（控制感，零件可单独拿在手里；不用 Spring AI 因抽象层厚、约定优先）
- D0 架构：单模块，不拆微服务、不上 Nacos / Gateway / admin
- D0 冻结范围：前端17页 / admin计费审计 / RAG / 多Agent编排 / 知识库CRUD
- D0 核心目标：把 AgentKernel 从"模拟配置"变成"真实配置驱动 + 有边界 + 可评估"
- D0 验收标准：改DB/YAML里的systemPrompt → 不重启 → refreshAgent() → 同sessionId下一轮行为变化 → Evals通过率不掉
- D0 工作分工：白天纯内网做设计（Evals用例、配置模型、对照表）；晚上连外网做实现（调API、跑测试）
- D1 代码来源规则：样板代码（依赖/配置/DTO）可用生成；核心逻辑（ReAct循环/保险丝/配置热更新/Evals断言）必须手写并通过"关屏默写"检验
- D1 红线：代码中禁止出现 ip:port / 模型名 / key 字面量，一律进 application.yml；空 class 不计入进度
- D1 补充红线：配置外置 ≠ 配置驱动。把 IP 搬进 yml 只是"外置"；只有当 systemPrompt / maxRounds / allowedTools 也从配置读出来并真正影响行为，才叫"驱动"，才是 D2 的验收。

### D2 阶段追加（本版新增）
- **D2 验收判据收紧**：`build()` 里"接线"（如 `.systemMessageProvider(...)`）只是**必要条件，不是充分条件**。验收只认**运行时证据**——改 systemPrompt 后同 sessionId 下一轮输出行为确实变化，日志入本文件第四节。无证据 = 未关账。
- **D2 死配置定义**：`AgentConfig` 每个字段必须在 `build()` 被消费，否则是死配置，**类型改对也不计入进度**。当前 `temperature`、`maxRounds` 属此类。
- **D2 新增红线（HEAD 完整性）**：验收前提是先 `git fetch` 钉死 HEAD 并确认本地无待推送提交。HEAD 漂移期间禁止以"我 push 了"作为进度依据；commit message 与实际 diff 不符（把旧改动记到新 commit 头上）按"叙述冒充落地"处理，不计入进度。

---

## 二、14天计划（不变）
- D1 建仓库 + 跑通官方天气示例 [已完成]
- D2 干掉 getConfig() 的 if-else（ConfigRepository 替代）  <- **当前位置，仍未关账**
- D3 refreshAgent() 热更新
- D4 保险丝：maxSequentialToolInvocations(5)
- D5 重复调用检测 + 工具返回值引导
- D6 工具异常收口（返回短句而非堆栈）
- D7 超时：OpenAiChatModel.timeout(30s)
- D8-D10 EvalTest.java：30条用例，跑通过率
- D11-D12 日志：每轮记录工具名/耗时/TokenUsage
- D13 手写 vs 框架对照表（面试资产）
- D14 README：只讲一件事——这个内核解决什么问题

---

## 三、当前状态（本版覆盖更新；锚定 25b6178 快照，全部"待重测"）

**已完成（有运行时证据）**
- D1 正式关账：`[TOOL CALLED] 成都` 已入自检记录（见第四节），工具链路真实打通。

**D2 骨架（代码层面已落地，但 HEAD 不稳定，待重测）**
- `AgentKernel.build` / `ConfigRepository.load` / `ToolRegistry` 注册与白名单过滤：均有真实逻辑（25b6178 快照）。
- `systemMessageProvider(s -> cfg.getSystemPrompt())`：代码已接线（25b6178 快照）——**但无运行时证据，不计入验收**。
- groupId 统一 `com.ggzed`、pom 死依赖清零、README 编码可显示。

**D2 未关账项（缺口全在这里，不在代码）**
1. **主验收未成立**：缺"改 systemPrompt → 同 sessionId 行为真变"的运行时证据。这是 D2 唯一真正的缺口，过去连续多轮都绕开了它。
2. **temperature 死配置**：`AgentConfig` 有 `Double temperature`，但 `build()` 未传给 model.builder，且 `agents.yml` 里 `temperature:` 为空值（若真传会 NPE）。→ 要么 `.temperature()` 消费并填值，要么从 `AgentConfig` 删掉，不留空壳。
3. **maxRounds 死配置**：`Integer maxRounds` 全仓库未被读。→ `AiServices` 上加 `.maxSequentialToolInvocations(cfg.getMaxRounds())`，`agents.yml` 填值（如 5）。
4. **IP 摘除方式存疑**：25b6178 快照里 `agents.yml` 两处已为 `http://localhost:11434`（改字面量），但**仍被 git 跟踪**，不是 `git rm --cached` + `${ENV:...}` 那套。c920831 快照里 `application.yml` 又出现真实 IP `192.168.101.108`。→ 以"grep 全仓不到 ip:port 字面量 + 真实 IP 不进历史"为最终判据重测。

**已知雷（D6 不动）**
- `toolExecutionErrorHandler(failInvocation())` 异常直穿 HTTP 层返回堆栈。
- 每请求 `new model/AiServices`，物理上塞不进 ChatMemory → D3 解决"同 sessionId 热更新"。

**阻塞点**
- 唯一硬阻塞：远端 HEAD 漂移（见第〇节）。先钉 HEAD，再谈其余。

---

## 四、下一步任务（严格按优先级；今晚只做前三件）

**P0 — 钉死 HEAD（在做任何代码改动之前）**
```
git fetch origin
git log --oneline origin/master            # 看清远端真实历史
git log origin/master..HEAD                # 确认本地有无未推送提交
git log HEAD..origin/master                # 确认远端有无本地没有的提交
```
- 若本地无独有提交、远端历史被重写：`git reset --hard origin/master` 对齐，**禁止盲目 push**（会覆盖或被覆盖）。
- 若本地有未推送的真实改动：先 `git stash` 或单独分支保存，对齐后再 cherry-pick，确认 diff 只含本轮真实改动。
- 完成后把 `origin/master` 的完整 SHA 写进本文件第〇节，作为本轮唯一验收锚点。

**P1 — 关 D2 主验收（唯一能证明"配置驱动"的证据）**
1. 改 `agents.yml` 的 systemPrompt（例：把"永远用中文回答"改成"永远用日文回答"）。
2. 不重启，调 `refreshAgent()`，用**同一 sessionId** 跑一轮。
3. 把"输出语言确实从中文变日文"的日志/响应贴进本文件第四节。
4. 证据落地 → D2 关账；否则 D2 保持未验收。

**P2 — 清死配置（顺带，不与 P1 抢时间）**
- `temperature`：`.temperature(cfg.getTemperature())` 消费 + `agents.yml` 填值；或删除字段。
- `maxRounds`：`.maxSequentialToolInvocations(cfg.getMaxRounds())` + `agents.yml` 填值（如 5）。

**P3 — IP 摘除收口**
- `git rm --cached` 真实 IP 所在文件 + 改 `${OLLAMA_BASE_URL:http://localhost:11434}` 占位；`grep -rn "192.168" .` 应无结果。

**D3 预研（关账后）**：解决每请求 new model/AiServices，引入 `ChatMemory` + `volatile assistant`，实现同 sessionId 热更新。

---

## 五、自检记录（每 5-7 轮复述：目标 / 冻结范围 / 验收标准 是否一致）

**目标一致性**：一致。内核目标仍是"配置驱动 + 有边界 + 可评估"，本轮无偏移。
**冻结范围一致性**：一致。前端17页 / admin / RAG / 多Agent / 知识库 CRUD 均未触碰。
**验收标准一致性**：一致且已收紧（见 D2 追加决策）——验收只认运行时证据，接线不算。

**工具真实调用证据（D1，已入档）**
```
2026-11-14T00:49:58.245+08:00  INFO 4140 --- [nio-8080-exec-2] com.ggzed.agent.tools.WeatherTool : [TOOL CALLED] 成都
```

**D2 主验收证据**：（待补——P1 完成后把"改 prompt 行为真变"的日志贴在此处）
```
（空。这是 D2 唯一未闭环的证据，过去多轮缺的就是这一行。）
修改前
agentId=weather&msg=%E6%88%90%E9%83%BD%E7%9A%84%E5%A4%A9%E6%B0%94%E6%80%8E%E4%B9%88%E6%A0%B7
The current weather in Chengdu is 26°C.
修改后
agentId=weather&msg=成都的天气怎么样
目前成都的温度是26度。
```

**远端 HEAD 锚点**：（P0 完成后填 `origin/master` 完整 SHA；当前两端点矛盾，暂记 tree=25b6178 / commits=c920831）

---

## 六、行为模式提醒（连续多轮同一条，本轮仍成立）

过去连续多轮的共同模式：**用"清依赖、改类型、删孤儿类、挪配置、摘 IP"这类装修工作，替代 D2 核心那一行——"改 prompt 行为真变"的运行时验证**。代码层面接线其实早已存在，真正的缺口从来不是代码，而是**没跑那一次验证**。

本轮再次提醒：今晚请**只对着第四节 P1**，别再碰任何边角。P1 那一行日志落地，D2 才关账；不落地，无论仓库表面多干净，D2 依旧是未验收。
