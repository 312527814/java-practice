# Workflow（StateGraph）示例说明

## 功能概述

在 `org.example.apptest.TestWorkflow` 中提供一个可独立运行的工作流示例，演示如何基于 Spring AI Alibaba Graph（Agent Framework 底层运行时）将 `ReactAgent` 与自定义 `NodeAction` 编排为顺序工作流，并展示 `ReplaceStrategy` 对同一状态键二次写入时的覆盖效果。

适用版本：`spring-ai-alibaba-agent-framework` **1.1.2.3**（与本模块 `pom.xml` 一致）。

参考文档：[工作流（Workflow）](https://java2ai.com/docs/frameworks/agent-framework/advanced/workflow)

## 业务场景

对一组业务经营数据做三阶段处理：

1. **数据分析**：`data_analyzer` Agent 解读输入指标，写入 `analysis_result`（第一次）。
2. **补充覆盖**：`supplement` 自定义节点再次写入同名键 `analysis_result`；因策略为 `ReplaceStrategy`，上一步分析结论被覆盖。
3. **报告生成**：`report_generator` Agent 读取覆盖后的 `analysis_result`，写入 `final_report`。

## 流程结构

```
START → data_analyzer → supplement → report_generator → END
```

状态变化示意：

| 步骤 | 节点 | 对 `analysis_result` 的操作 | 结果 |
|------|------|-----------------------------|------|
| 1 | `data_analyzer` | `outputKey` 写入模型分析结果 | 首次赋值 |
| 2 | `supplement` | `return Map.of("analysis_result", "补充：…")` | **Replace 覆盖**为补充文案 |
| 3 | `report_generator` | 只读 `{analysis_result}`，写 `final_report` | 报告基于覆盖后的值 |

- 核心状态键：`input`、`analysis_result`、`final_report`
- 状态更新策略均为 `ReplaceStrategy`
- Agent 通过 `asNode(true, false)` 嵌入父图；`supplement` 通过 `node_async(new SupplementNode())` 注册

## 运行方式

1. 配置环境变量 `ALI_AI_KEY`（DashScope API Key）。
2. 运行 `org.example.apptest.TestWorkflow` 的 `main` 方法。
3. 控制台会打印 `[supplement] 覆盖前/即将写入`，以及最终 `analysis_result` 与 `final_report`。

## 与现有 apptest 的关系

| 示例类 | 侧重点 |
|--------|--------|
| `TestAgent` | 单 Agent + Tool 调用 |
| `TestHumanInTheLoop` | Agent 工具审批中断与恢复 |
| `TestWorkflow` | StateGraph + Agent Node + 自定义 Node（Replace 演示） |
| `TestFlowAgent` | FlowAgent：顺序 / 并行 / 路由（详见 `flow-agent-需求说明.md`） |

## 扩展方向

- 将 `analysis_result` 改为 `AppendStrategy`，对比二次写入后变成列表而非覆盖。
- 增加条件边，按分析结果分流到不同报告节点。
- 为 `CompileConfig` 注册 `MemorySaver`，支持断点续跑与人工确认。
