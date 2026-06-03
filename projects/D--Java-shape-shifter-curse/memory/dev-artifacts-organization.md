---
name: dev-artifacts-organization
description: 非项目产出的代码/文件应移入 _dev/ 目录统一管理
metadata:
  type: feedback
---

所有不属于项目主源码的文件（反编译代码、3D模型源文件、开发脚本、参考配置、示例数据包、从属服务器代码等）都应移入项目根目录的 `_dev/` 目录下。

**Why:** 保持项目根目录干净，避免 GitHub 仓库中出现无关文件（如反编译的 Apoli/AzureLib/EMF/ETF 代码、Eclipse IDE 配置、缓存文件等）。项目的 CI、用户 fork、协作者查看时不应看到这些开发参考材料。

**How to apply:**
- `Decompiler/` — 反编译的外部Mod源码 → `_dev/decompiled/`
- `3d_models/` — 3D模型源文件 → `_dev/3d_models/`
- `PatronServer/` — 赞助者服务器代码 → `_dev/PatronServer/`
- `custom_form_pack_example/` — 形态资源包示例 → `_dev/custom_form_pack_example/`
- `tools/` — 开发工具脚本 → `_dev/tools/`
- `*reference*` / 被追踪的外部Mod配置文件 → `_dev/<name>-reference/`
- `.settings/` — Eclipse IDE 配置 → `_dev/eclipse-settings/`
- 各种缓存文件（`__pycache__/`, `.sonarlint/` 等）→ 加入 `.gitignore` 或直接删除
- `libs/` — 本地库JAR → `_dev/libs/`
- `run/` — Minecraft运行目录 → 已在 `.gitignore` 中

通过 `git mv` 移动已追踪的文件以保留历史记录，未追踪/已忽略的文件直接用 `mv` 移动。移动后更新 `.gitignore` 并提交。
