# Shape Shifter Curse Mod for Minecraft Java Edition
The GitHub repository for the ShapeShifterCurse mod, adapted for Forge/Fabric (stability in cross-environment scenarios is being verified).

This branch is a **community-maintained fork** (not official) and is under continuous development. Since the development is done in my spare time from studying, updates may be relatively slow.

## Key Notes for This Fork
### Compatibility
- Supported Minecraft versions: 1.20.x (main focus, other versions to be verified)
- Mod loaders: 
  - Fabric: Basic functionality is stable; 
  - Forge: Adapted for cross-loader compatibility (resolved crashes caused by type inference/Lambda parameter ambiguity, Mixin annotation conflicts, etc.);
  - Sinytra-Connector environment: Preliminary compatibility adjustments completed (e.g., replaced MixinExtras annotations with native Mixin APIs, fixed button click crashes, supplemented missing accessors for Apollo/Origins dependencies).

### Usage
- To create custom forms via datapacks and resource packs, refer to the [official wiki](https://ssc-wiki.readthedocs.io/en/latest/) .
- This mod is open-source and free of charge. **Do not trust third-party channels that require payment for downloads** — all legitimate releases are available on GitHub/Modrinth/CurseForge.

### Known Issues
- Forge: compatibility with other mods (e.g., Apollo) needs further testing;
- Sinytra-Connector: Full functional verification is pending (e.g., shape switching);
- Mixin conflicts may occur with some rendering mods (resolved for `EntityRenderDispatcherAccessor` conflicts, other conflicts to be addressed on a case-by-case basis).

---

# 幻形者诅咒 Minecraft Java版Mod
本仓库是幻形者诅咒（ShapeShifterCurse）模组的**社区维护分支**（非官方），适配Forge/Fabric双加载器（跨环境稳定性仍在验证中）。

本分支持续开发中，因开发在学业空余时间进行，更新进度较慢，敬请谅解。

## 本分支核心说明
### 兼容性
- 支持的MC版本：1.20.1（主力维护版本，其他版本待验证）；
- 加载器适配：
  - Fabric端：基础功能稳定；
  - Forge端：已完成跨加载器兼容适配（修复了类型推断/Lambda参数模糊导致的崩溃、Mixin注解冲突等问题）；
  - 信雅互联环境：已完成初步兼容调整（如替换MixinExtras注解为原生Mixin、修复按钮点击崩溃、补全Apollo依赖缺失的访问器）。

### 使用指南
- 如需通过数据包/资源包创建自定义形态， [中文版wiki](https://ssc-wiki.readthedocs.io/zh-cn/latest/)；
- 本模组开源且完全免费，**切勿相信任何要求付费下载的第三方渠道** — 所有合法版本均发布于GitHub/Modrinth/CurseForge。

### 已知问题
- 信雅互联环境：功能验证待完成（如变形玩法等）；
- 与部分渲染类模组可能存在Mixin冲突（已解决`EntityRenderDispatcherAccessor`冲突，其余冲突需个案处理）。
