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

> **分支 / Branch**: `ver/1.21.1_Apoli2.12.0-alpha7`

| 状态 | 模块 | 说明 |
|------|------|------|
| ✅ | Build 系统 | Gradle 依赖更新至 1.21.1，Java 17→21 |
| ✅ | Satin 2.0 | `ladysnake.satin` → `org.ladysnake.satin` |
| ✅ | CCA 6.x | `dev.onyxstudios.cca` → `org.ladysnake.cca`，方法签名 + RegistryWrapper 适配 |
| ✅ | Advancement | JSON 迁移至 Codec (`getConditionsCodec`)，`getId()` override 移除 |
| ✅ | Fabric API | `FabricItemSettings` → `Item.Settings`，`TooltipContext`/`FoodComponent` 适配 |
| ✅ | AzureLib 3.0.25 | 包路径 `renderer`/`model`/`cache.object` → `common.api.client` |
| ✅ | ArmorMaterial | `interface` → `class` (构造器模式)，`RegistryEntry` 注册，durability 显式设置 |
| ✅ | Apoli 2.12.0 | `ConditionTypes` → `ApoliRegistries`，`NamespaceAlias`/`CriteriaRegistryInvoker` 移除 |
| ✅ | PotionUtil | → `DataComponentTypes.POTION_CONTENTS` + `PotionContentsComponent` |
| ✅ | Registry API | `Identifier` → `RegistryKey`，`Registry.register()` 参数迁移 |
| ✅ | Networking | `ServerPlayNetworking.send()` → `BytePayload` CustomPayload + PayloadTypeRegistry |
| ✅ | CursedMoon | 恢复，ModPacketsS2CServer 调用还原 |
| ✅ | TransformManager | 恢复，ServerPlayNetworking 迁移 + InstinctTicker 恢复 |
| ✅ | 实体系统 | TransformativeBat/Axolotl/Ocelot/Spider 恢复，Wolf API 部分 stub |
| ✅ | Items / Blocks | `TooltipContext`/`FoodComponent`/`FabricItemSettings` 全面适配 |
| ✅ | UI 屏幕 | BookOfShapeShifter, NormalFormSelect, PatronFormSelect, ConfigMenu 恢复 |
| ✅ | Enchantment | WaterProtection → JSON 数据文件 (`data/origins/enchantment/`) |
| 🚧 | Client 入口 | `ShapeShifterCurseFabricClient` 禁用 (EntityRenderer/Feature 依赖待修复) |
| 🚧 | Recipe/Brewing | `Recipe` final class，BrewingRecipeUtils 禁用 |
| 🚧 | Origins Badge | Badge 系统禁用 (Apoli 2.12.0 SerializableData API 变更) |
| 🚧 | 部分 Power | ItemStorePower, VirtualTotemPower 等 ~10 个 Power 待恢复 |
| 🚧 | Entity Renderer | BatEntityRenderer (setupTransforms), TSpiderEntityRenderer 待修复 |

> **编译状态**: 8 个错误，全部为 `ShapeShifterCurseFabricClient` 被禁用后的引用缺失

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
