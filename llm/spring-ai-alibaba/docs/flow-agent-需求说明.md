# FlowAgent（顺序 / 并行 / 路由）示例说明

## 功能概述

在 `org.example.apptest.TestFlowAgent` 中提供 FlowAgent 编排示例，覆盖 Agent Framework 内置的三种工作流 Agent：

| 模式 | 类 | 作用 |
|------|-----|------|
| 顺序 | `SequentialAgent` | 子 Agent 按固定顺序依次执行，后置可引用前置 `outputKey` |
| 并行 | `ParallelAgent` | 多个子 Agent 同时处理同一输入，再合并结果 |
| 路由 | `LlmRoutingAgent` | 由 LLM 根据用户意图选择最合适的子 Agent |

适用版本：`spring-ai-alibaba-agent-framework` **1.1.2.3**。

参考文档：[多智能体（Multi-agent）](https://java2ai.com/docs/frameworks/agent-framework/advanced/multi-agent)

## 场景说明

### 1. 顺序（SequentialAgent）

```
input → writer_agent(article) → reviewer_agent(reviewed_article)
```

- 写作 Agent 根据 `{input}` 生成散文，写入 `article`
- 评审 Agent 读取 `{article}`，润色后写入 `reviewed_article`

### 2. 并行（ParallelAgent）

```
input ─┬→ prose_writer_agent  (prose_result)
       ├→ poem_writer_agent   (poem_result)
       └→ summary_agent       (summary_result)
              ↓
        DefaultMergeStrategy → merged_results
```

- 三个创作 Agent 并行执行
- 通过 `RunnableConfig.addParallelNodeExecutor` 注入线程池
- 使用 `ParallelAgent.DefaultMergeStrategy` 合并到 `merged_results`

### 3. 路由（LlmRoutingAgent）

```
input → LLM 路由决策 → writer / reviewer / translator（择一）
```

- 子 Agent 通过 `description` 声明专长，供路由模型选择
- 示例分别发起写作、修改、翻译三类请求，观察分流结果键：
  - `writer_output` / `reviewer_output` / `translator_output`

## 运行方式

1. 配置环境变量 `ALI_AI_KEY`
2. 运行 `org.example.apptest.TestFlowAgent`
3. 可选参数：
   - 无参数或 `all`：三种模式都跑
   - `sequential` / `parallel` / `routing`：只跑一种

## 与现有示例关系

| 示例类 | 侧重点 |
|--------|--------|
| `TestAgent` | 单 ReactAgent + Tool |
| `TestWorkflow` | 底层 StateGraph 手工编排 |
| `TestFlowAgent` | 高层 FlowAgent（顺序 / 并行 / 路由） |

`TestWorkflow` 偏底层图 API；`TestFlowAgent` 偏 Agent Framework 声明式编排，二者互补。
