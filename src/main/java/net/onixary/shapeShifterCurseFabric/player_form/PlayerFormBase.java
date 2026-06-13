package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 所有玩家形态的基类。每个形态继承此类并覆写动画生命周期方法以定义自身行为。
 * <p>
 * 形态的核心属性包括：{@link PlayerFormPhase 阶段}、{@link PlayerFormBodyType 体型}、
 * 各类功能标志（缓降、手部动画覆盖、冲刺猛冲等）。
 * <p>
 * 动画生命周期由三个钩子组成，依次为：
 * <ol>
 *   <li>{@link #getAnimStateController} — 返回当前状态的动画控制器</li>
 *   <li>{@link #registerPowerAnim} — 注册 Power 动画（触发时调用）</li>
 *   <li>{@link #getPowerAnim} — 根据 Power 动画 ID 返回具体动画</li>
 * </ol>
 *
 * @see PlayerFormDynamic JSON 数据包驱动的形态（继承此类）
 * @see PlayerFormGroup 形态组
 */
public class PlayerFormBase {
	/**
	 * 形态唯一 ID，例如 {@code ssc:axolotl_0}。通常与 Origin Power ID 关联。
	 */
    public Identifier FormID;
	/** 所属形态组，通过 {@link #setGroup(PlayerFormGroup, int)} 绑定，仅可设一次。 */
    public PlayerFormGroup Group = null;
	/** 在所属形态组中的索引位置。对应 {@link PlayerFormPhase} 的 INDEX_* 常量。 */
    public int FormIndex = 0;

    private PlayerFormPhase Phase = PlayerFormPhase.PHASE_CLEAR;
    private PlayerFormBodyType BodyType = PlayerFormBodyType.NORMAL;
    private boolean HasSlowFall = false;
    private boolean OverrideHandAnim = false;
    private boolean CanSneakRush = false;
    private boolean CanRushJump = false;
    private boolean IsCustomForm = false;
    private boolean IgnoreCursedMoon = false;
    private boolean IgnoreCatalyst = false;

    private boolean IsRegisteredPowerAnim = false;

    public String Origin_NameSpace_OverWrite = null;
    public Identifier OriginLayer_OverWrite = null; // Default: "origins:origin"

    public PlayerFormBase(Identifier formID) {
        FormID = formID;
    }

	/** @return 形态体型类型，决定使用 NORMAL 还是 FERAL 渲染和动画路径 */
    public PlayerFormBodyType getBodyType() {
        return BodyType;
    }

    public PlayerFormBase setBodyType(PlayerFormBodyType bodyType) {
        BodyType = bodyType;
        return this;
    }

    public PlayerFormPhase getPhase() {
        return Phase;
    }
    public PlayerFormBase setPhase(PlayerFormPhase phase) {
        Phase = phase;
        return this;
    }

    // 暂时在PlayerForm实现文本

	/**
	 * 获取在玩家图鉴（Codex）中显示的文本内容。
	 * <p>
	 * 语言键格式：{@code codex.form.<namespace>.<path>.<type>}
	 *
	 * @param type 内容类型（TITLE / APPEARANCE / PROS / CONS / INSTINCTS）
	 * @return 本地化文本
     */
    public Text getContentText(CodexData.ContentType type) {
        // Lang 格式 codex.form.<ModID>.<FormId>.<type>
        return Text.translatable("codex.form." + FormID.getNamespace() + "." + FormID.getPath() + "." + type.toString().toLowerCase());
    }


	/**
	 * 获取指定动画状态对应的控制器。
	 * <p>
	 * 子类覆写此方法，根据 {@code animStateID} 返回对应的 {@link AbstractAnimStateController}。
	 * 返回 null 表示该状态没有覆盖动画，使用默认行为。
	 *
	 * @param player         目标玩家
	 * @param animSystemData 动画系统数据
	 * @param animStateID    动画状态 ID
	 * @return 动画状态控制器，无覆盖则返回 null
	 * @see AbstractAnimStateController
	 * @see net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum
     */
    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        return null;
    }

	/**
	 * 注册此形态所需的 Power 动画。在形态度量初始化时调用。
	 * <p>
	 * 子类可在此处构建并缓存 {@link AnimationHolder} 实例。
	 *
	 * @param player         目标玩家
	 * @param animSystemData 动画系统数据
	 * @see #getPowerAnim
     */
    public void registerPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        this.IsRegisteredPowerAnim = true;
    }

	/** @return 是否已调用过 {@link #registerPowerAnim} */
    public boolean isPowerAnimRegistered(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        return IsRegisteredPowerAnim;
    }

	/**
	 * 获取指定 Power 动画 ID 对应的动画实例。
	 * <p>
	 * 返回值的 Pair 中：左值为是否匹配（false 则由 Power 注册表提供默认动画），右值为动画实例。
	 *
	 * @param player         目标玩家
	 * @param animSystemData 动画系统数据
	 * @param powerAnimID    Power 动画 ID
	 * @return Pair.left = 是否命中此形态的动画；Pair.right = 动画持有者（可为 null）
     */
    public @NotNull Pair<Boolean, @Nullable AnimationHolder> getPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier powerAnimID) {
        return new Pair<>(false, null);
    }

    public boolean getHasSlowFall() {
        return HasSlowFall;
    }

    public PlayerFormBase setHasSlowFall(boolean hasSlowFall) {
        HasSlowFall = hasSlowFall;
        return this;
    }

    public boolean getOverrideHandAnim() {
        return OverrideHandAnim;
    }

    public PlayerFormBase setOverrideHandAnim(boolean overrideHandAnim) {
        OverrideHandAnim = overrideHandAnim;
        return this;
    }

    public boolean getCanSneakRush() {
        return CanSneakRush;
    }

    public PlayerFormBase setCanSneakRush(boolean canSneakRush) {
        CanSneakRush = canSneakRush;
        return this;
    }

    public boolean getCanRushJump() {
        return CanRushJump;
    }

    public PlayerFormBase setCanRushJump(boolean canRushJump) {
        CanRushJump = canRushJump;
        return this;
    }

    public boolean getIsCustomForm() {
        return IsCustomForm;
    }

    public PlayerFormBase setIsCustomForm(boolean isCustomForm) {
        IsCustomForm = isCustomForm;
        return this;
    }

    public boolean getIgnoreCursedMoon() {
        return IgnoreCursedMoon;
    }

    public PlayerFormBase setIgnoreCursedMoon(boolean ignoreCursedMoon) {
        IgnoreCursedMoon = ignoreCursedMoon;
        return this;
    }

    public boolean getIgnoreCatalyst() {
        return IgnoreCatalyst;
    }

    public PlayerFormBase setIgnoreCatalyst(boolean ignoreCatalyst) {
        IgnoreCatalyst = ignoreCatalyst;
        return this;
    }

    public PlayerFormGroup getGroup() {
        return Group;
    }

    public int getIndex() {
        return FormIndex;
    }

	/**
	 * 将此形态绑定到指定形态组并设置索引。
	 * <p>
	 * 仅可调用一次，重复调用会抛出异常。在 {@link PlayerFormGroup#addForm} 中自动调用。
	 *
	 * @param group     目标形态组
	 * @param formIndex 形态在组内的索引
	 * @throws IllegalArgumentException 如果该形态已被绑定到其他组
     */
    public void setGroup(PlayerFormGroup group, int formIndex) {
        if (Group != null) {
            throw new IllegalArgumentException("Group already set");
        }
        Group = group;
        FormIndex = formIndex;
    }

	/**
	 * 获取披风（Cape）在玩家身上的偏移位置。
	 * <p>
	 * 返回的是相对于玩家骨骼根部的偏移量。FERAL 形态由于四足姿势，披风位置需下移并前移。
	 *
	 * @param player 客户端玩家实体
	 * @return 披风偏移坐标（FERAL: y=-0.2, z=0.3；NORMAL: z=0.125）
     */
    public Vec3d getCapeIdleLoc(AbstractClientPlayerEntity player) {
        if (getBodyType() == PlayerFormBodyType.FERAL) {
            return new Vec3d(0.0f, -0.18f, 0.35f);
        }
        else {
            return new Vec3d(0.0, 0.0, 0.125);
        }
    }

	/**
	 * 获取披风的基础旋转角度。
	 * FERAL 形态下披风需要旋转 90° 以适配四足姿势的背部方向。
	 *
	 * @param player 客户端玩家实体
	 * @return 旋转角度（度数），FERAL 为 90°，NORMAL 为 0°
     */
    public float getCapeBaseRotateAngle(AbstractClientPlayerEntity player) {
	    return 0.0f;
    }

	/** @return FERAL 形态时需要在渲染过程中旋转 X 轴角度以适配四足姿势 */
    public boolean NeedModifyXRotationAngle() {
        return getBodyType() == PlayerFormBodyType.FERAL;
    }

	/**
	 * 比较两个形态是否相等（基于 {@link #FormID}）。
	 *
	 * @param o 比较对象
	 * @return 如果 {@code o} 是 {@link PlayerFormBase} 且 FormID 相同则返回 true
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerFormBase) {
            return ((PlayerFormBase)o).FormID.equals(FormID);
        }
        return false;
    }

	/** @return FormID 的字符串表示（不一定是语言文件中的显示名称） */
    public String getIDString() {
        return FormID.toString();
    }

	/**
	 * 获取形态的显示名称。
	 * <p>
	 * 语言键格式：{@code codex.form.<namespace>.<path>.name}
	 *
	 * @return 本地化显示名称
     */
    public Text getFormName() {
        return Text.translatable("codex.form." + FormID.getNamespace() + "." + FormID.getPath() + ".name");
    }

	/**
	 * 覆盖此形态关联的 Origin ID 的命名空间。
	 * <p>
	 * 用于数据包形态指定 Origin 的命名空间，默认使用 FormID 的命名空间。
	 *
	 * @param nameSpace 目标命名空间
	 * @return this（链式调用）
     */
    public PlayerFormBase setOriginNameSpaceOverWrite(String nameSpace) {
        Origin_NameSpace_OverWrite = nameSpace;
        return this;
    }

	/**
	 * 覆盖此形态关联的 Origin Layer ID。
	 * <p>
	 * 默认使用 {@code origins:origin}。
	 *
	 * @param OriginLayerID 目标 Layer ID
	 * @return this（链式调用）
     */
    public PlayerFormBase setOriginLayerOverWrite(Identifier OriginLayerID) {
        OriginLayer_OverWrite = OriginLayerID;
        return this;
    }

	/**
	 * 获取此形态关联的 Origin Power ID。
	 * <p>
	 * 格式为 {@code <namespace>:form_<path>}。例如 {@code ssc:form_axolotl_0}。
	 *
	 * @return Origin Power ID
     */
    public Identifier getFormOriginID() {
        String NameSpace = Origin_NameSpace_OverWrite != null ? Origin_NameSpace_OverWrite : FormID.getNamespace();
        return Identifier.of(NameSpace, "form_" + FormID.getPath());
    }

	/**
	 * 获取此形态使用的 Origin Layer ID。
	 * <p>
	 * 默认返回 {@code origins:origin}。
	 *
	 * @return Origin Layer ID
     */
    public Identifier getFormOriginLayerID() {
        return OriginLayer_OverWrite != null ? OriginLayer_OverWrite : Identifier.of(Origins.MODID, "origin");
    }
}
