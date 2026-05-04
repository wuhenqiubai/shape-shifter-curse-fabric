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

## Minecraft 1.21.1 移植进度 / Porting Progress

> **分支 / Branch**: `ver/1.21.1_Apoli2.12.0-alpha9`

| 状态 | 模块 | 说明 |
|------|------|------|
| ✅ | Build 系统 | Gradle 依赖更新至 1.21.1，Java 17→21 |
| ✅ | Satin 2.0 | `ladysnake.satin` → `org.ladysnake.satin` |
| ✅ | CCA 6.x | `dev.onyxstudios.cca` → `org.ladysnake.cca`，方法签名适配 |
| ✅ | Advancement | JSON 迁移至 Codec (`getConditionsCodec`) |
| ✅ | Fabric API | `FabricItemSettings` → `Item.Settings`，`TooltipContext` 适配 |
| ✅ | AzureLib 3.0.25 | 包路径 `renderer`/`model`/`cache.object` → `common.api.client` |
| ✅ | ArmorMaterial | `interface` → `class` (构造器模式)，`RegistryEntry` 注册 |
| ✅ | Apoli 2.12.0 | `ConditionTypes` → `ApoliRegistries`，`NamespaceAlias` 移除 |
| ✅ | PotionUtil | → `DataComponentTypes.POTION_CONTENTS` |
| ✅ | Registry API | `Identifier` → `RegistryKey` |
| 🚧 | Enchantment | `Enchantment` 变为 final class，需数据驱动重写 |
| 🚧 | Recipe/Brewing | `Recipe` 变为 final class，酿造系统重构 |
| 🚧 | ManaComponent | CCA RespawnableComponent 方法签名对齐 |

---

## 非官方维护声明 / Unofficial Maintenance Notice

**本分支是社区维护的 1.21.1 移植版本，非官方发布。**

- 此移植由社区贡献者通过自动化工具辅助完成，**未经官方开发者 (onixary) 审核或认可**
- **不保证稳定性**：可能存在未发现的 bug、兼容性问题或游戏崩溃
- **不保证持续维护**：此移植可能不会跟进上游更新或修复所有已知问题
- 使用前请备份存档，生产环境使用需自行评估风险
- 欢迎提交 Issue 或 PR 协助改进移植质量

**This branch is a community-maintained 1.21.1 port and is NOT an official release.**

- This port was completed by community contributors with automated tooling assistance, **not reviewed or endorsed by the original author (onixary)**
- **No stability guarantee**: undiscovered bugs, compatibility issues, or crashes may exist
- **No maintenance guarantee**: this port may not follow upstream updates or fix all known issues
- Please back up your saves before use; evaluate risks yourself for production use
- Issues and PRs are welcome to help improve port quality

---

## 移植贡献者 / Port Contributors

| 贡献者 | 角色 |
|--------|------|
| [wuhenqiubai](https://github.com/wuhenqiubai) | 移植维护 / Port Maintainer |
| Claude Code (Anthropic) | 自动化移植辅助 / Automated Porting Assistance |

> 移植工具链: Claude Code + deepseek-v4-pro, Fabric Loom 1.9, Yarn mappings 1.21.1+build.3
