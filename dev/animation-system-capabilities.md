# SSC 动画系统功能清单

> 更新日期: 2026-06-07

---

## 架构

```
渲染层         AzureLib (旧 API)
              ├─ GeoModel/GeoBone/GeoObjectRenderer  ← 形态模型渲染
              ├─ AzAnimatorAccessor + FormAzAnimator  ← 已就位，未接线
              └─ DefaultModelAnimationSystem (DMS)    ← GeoBone←→PAL 桥梁

动画数据/控制  PAL 1.1.4
              ├─ AnimSystem (FSM: OnGround/InAir/UseItem)
              ├─ PlayerAnimationController (ModifierLayer)
              ├─ AnimationHolder (speed/fade/easing/skipFade 配置)
              ├─ AnimationHolderData (build() 工厂)
              └─ AnimUtils (JSON→动画持有者反序列化)

状态机         FSM
              ├─ OnGroundFSM → idle/walk/sprint/swim/sleep/ride/climb
              ├─ InAirFSM   → jump/fall/fly/elytra/swim
              ├─ UseItemFSM → mining/attack
              └─ FSMUtils.ProcessUniversalAnim() (sleep/ride/climb/swim)

过渡          AbstractFadeModifier.standardFadeIn(fade, easing)
              ├─ EasingType 缓存 (35 种可选的缓动曲线)
              └─ skipFade 跳过过渡 (Axolotl2/3 crawl)
```

---

## 已实现功能

### 1. 缓动系统

| 功能                | 说明                                                                                                       |
|-------------------|----------------------------------------------------------------------------------------------------------|
| 35 种 EasingType   | LINEAR, QUAD, CUBIC, QUART, QUINT, EXPO, SINE, CIRC, BACK, ELASTIC, BOUNCE 各 3 种 (+ In/Out/InOut)        |
| Bezier 缓动         | 三次贝塞尔曲线 (AzEasingUtil)                                                                                   |
| Catmull-Rom       | Catmull-Rom 平滑插值                                                                                         |
| 按动画类别分发           | walk→QUAD, run→EXPO, jump→BOUNCE, swim→SINE, fall→QUINT, attack→EXPO, climb→BACK, sleep→CUBIC, fly→QUART |
| 全局默认回退            | LINEAR (安全默认)                                                                                            |
| skipFade          | 跳过过渡直接切换 (Axolotl2/3 crawl)                                                                              |
| controller.stop() | FSM 无动画时清除残留                                                                                             |

### 2. 动画控制

| 功能       | 说明                              |
|----------|---------------------------------|
| FSM 三级分发 | OnGround / InAir / UseItem 自动切换 |
| 0动画回退    | FSM 无对应动画时返回 null → 回退原版        |
| NPPA     | Now Playing Power Animation 计时器 |
| 速度控制     | SpeedModifier 逐动画可调 (1.0~3.3x)  |
| fade 控制  | 逐动画可调 (2/4/6/10 tick)           |
| 变身动画     | AnimTransform 独立管理变形间过渡         |

### 3. 动画数据

| 功能          | 说明                                                           |
|-------------|--------------------------------------------------------------|
| 80 个 PAL 动画 | 17 个 FERAL common + 各形态特定动画                                  |
| 形态隔离        | FeralBase 统一四足共享动画，各形态独立覆盖                                   |
| JSON 反序列化   | AnimUtils.readAnim/readController 从数据包解析                     |
| Power 动画    | PlayerAnimInterfaceMixin 提供 playTime/Count/Loop/storyBack 接口 |
| 网络同步        | ModPacketsC2S/S2C 动画触发同步                                     |

### 4. 骨骼控制

| 功能      | 说明                                          |
|---------|---------------------------------------------|
| 6 主骨骼控制 | head/body/leftArm/rightArm/leftLeg/rightLeg |
| 额外骨骼映射  | ProcessExtraBone 通过 extraPartsMap           |
| 旋转反转    | invertRotForPart(3轴)                        |
| 硬编码偏移   | 手臂 ±5、腿部 ±2/±12                             |

### 5. 程序化动画 (伪物理)

| 功能     | 说明                                  |
|--------|-------------------------------------|
| 尾巴链    | 根骨 + N 节链骨，正弦摆动 + 拖尾阻尼              |
| 翅膀链    | 左右对称，垂直拖尾                           |
| 头尾链    | headTail 独立链                        |
| 阻尼模型   | 一阶低通 (past *= 0.75 + delta *= 0.55) |
| 6 可调参数 | 衰减率、跟随率、垂直率、最大摆幅、摆动频率、摆动幅度          |

### 6. 兼容性

| 功能              | 说明                     |
|-----------------|------------------------|
| EMF 暂停          | FERAL 全暂停 + FPM 第一人称暂停 |
| PAL 迁移          | 完全替代 PlayerAnimator    |
| AzureLib 3.0.39 | 从 3.0.19 无痛升级          |
| 缓动回退兼容          | 默认 LINEAR 保证旧动画无副作用    |

---

## 可扩展方向

| 优先级    | 功能                                   | 依赖                |
|--------|--------------------------------------|-------------------|
| **P0** | 二阶阻尼弹簧 (尾巴回弹)                        | 无，改 5 行           |
| **P0** | 链骨权重衰减 (根-尖渐变)                       | 无，改 3 行           |
| **P1** | MoLang 查询替代 FSM 硬编码                  | PAL MolangLoader  |
| **P1** | 逐轴过渡长度 (AdvancedPlayerAnimBone)      | PAL API           |
| **P1** | 动画链 (RawAnimation.thenPlay/thenLoop) | PAL API           |
| **P2** | PlayerAnimationFrame 替代 DMS 程序化动画    | PAL API           |
| **P2** | AzAnimator.setCustomAnimations() 接线  | 需 AzBakedModel 就位 |
| **P3** | GeoModel→AzBone 渲染管线迁移               | 需 14 文件联动         |
