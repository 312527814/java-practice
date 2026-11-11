# 自定义 Node Workflow 示例说明

## 功能概述

在 `org.example.apptest.TestCustomNodeWorkflow` 中演示如何实现自定义 `NodeAction`，并用 `node_async(...)` 挂到 `StateGraph`，配合条件边完成分流。

适用版本：`spring-ai-alibaba-agent-framework` **1.1.2.3**。

与 `TestWorkflow` 对比：

| 示例 | Node 来源 |
|------|-----------|
| `TestWorkflow` | `ReactAgent.asNode(...)` |
| `TestCustomNodeWorkflow` | 自己实现的 `NodeAction` 类 |

## 流程结构

```
START → preprocess → classify ─┬→ handle_complaint → END
                               ├→ handle_praise    → END
                               └→ handle_other     → END
```

## 自定义节点

| 节点类 | 节点名 | 职责 |
|--------|--------|------|
| `PreprocessorNode` | `preprocess` | 清洗 `input`，写入 `cleaned_input` |
| `KeywordClassifyNode` | `classify` | 按关键词分类，写入 `category`（complaint/praise/other） |
| `SummaryNode` | `handle_*` | 调用 ChatClient，按不同提示词生成 `summary` |

状态键均使用 `ReplaceStrategy`：`input`、`cleaned_input`、`category`、`summary`。

## 运行方式

1. 配置 `ALI_AI_KEY`
2. 运行 `TestCustomNodeWorkflow.main`
3. 默认输入为投诉类物流反馈，应走到 `handle_complaint` 并输出摘要

可自行修改 `input` 文案验证另外两条分支（好评 / 其他）。
