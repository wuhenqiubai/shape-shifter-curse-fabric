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

上游 Apoli 库升级至 2.12.0-pre.1，大量 API 发生破坏性变更（`Power` → `PowerType`、`PowerFactory` → `TypedDataObjectFactory` 等）。当前迁移进展：

| 阶段 | 内容 | 状态 |
|------|------|------|
| Phase 1 | 注册中心 (AdditionalPowers / EntityActions / Conditions / ItemCondition) | ✅ 完成 |
| Phase 2 | Origins 集成 (OriginsPowerTypes / CallbackPower / EntityConditions) | ✅ 完成 |
| Phase 3A | 简单 Power（~25 个，仅构造器逻辑） | 🔄 进行中 |
| Phase 3B | 中等 Power（~20 个，含 serverTick） | ❌ 未开始 |
| Phase 3C | 复杂 Power（~15 个，嵌套类/多字段） | ❌ 未开始 |
| Phase 4 | Action 迁移（13 个文件 → EntityActionType） | ✅ 完成 |
| Phase 5 | Condition 迁移（9 个文件 → EntityConditionType/ItemConditionType） | ✅ 完成 |
| Phase 6 | 编译验证 & 功能测试 | ❌ 未开始 |

> 完整迁移计划见 `.sisyphus/migration-plan.md`

---

*最后更新: 2026-04-30_OpenClaw*
