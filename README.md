# Shape Shifter Curse Mod for Minecraft Java version
GitHub repository for the mod Shape Shifter Curse, created for Fabric

This mod is under active development. As I'm only working on it during my spare time, updates may be slow

If you want to create custom forms using data packs and resource packs, please check the [wiki](https://ssc-wiki.readthedocs.io/en/latest/) here

This mod is open-source and free. Do not trust any third-party channels that charge for downloads

幻形者诅咒 Minecraft java版Mod
---
幻形者诅咒mod的Github仓库，适用于Fabric端

mod正在持续开发中。由于我只是业余时间开发，更新可能比较慢

如果你想要通过数据包与资源包创建自定义形态，请查阅这里的[中文版wiki](https://ssc-wiki.readthedocs.io/zh-cn/latest/)

本mod开源免费，请不要相信那些需要你付费下载的第三方渠道

---

> ⚠️ **非官方维护分支 — Minecraft 1.21.1 / Fabric 移植**
> 此分支由 [wuhenqiubai](https://github.com/wuhenqiubai) 维护，将上游 mod 移植到 **Minecraft 1.21.1 + Fabric 0.19.2 + Java 21**。如需原始版本请查看上游 [onixary/shape-shifter-curse-fabric](https://github.com/onixary/shape-shifter-curse-fabric)。

### Apoli 2.12.0 API 迁移进度
> 基本完成

### 剩余需要迁移的内容

| 类别 | 文件数 | 说明 | 进度 |
|------|--------|------|------|
| 🔴 Advancement 触发条件 | 17 个文件 | `PowerType<?>` 泛型、`AbstractCriterion` 1.21.1 改名 | 进行中 |
| 🟡 Origins 旧 API 残留 | 8 个文件 | `PowerTypeRegistry`、`ConditionTypes`、Badge 相关 | 进行中 |
| 🟠 Satin 2.0 API 变更 | 1 个文件 | `ShaderEffectRenderCallback` 等包路径更新 | 计划中 |
| 🟢 Minecraft 1.21.1 API 变更 | 散落各处 | `FabricItemSettings`、`Instrument` 等 | 计划中 |

> 参与移植的AI模型：DeepSeek-V4、GLM5.1；使用的AI工具：OpenClaw、OpenCode

---

*最后更新: 2026-04-30*
