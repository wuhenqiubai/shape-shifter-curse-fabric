package net.onixary.shapeShifterCurseFabric.player_form;

/**
 * 形态阶段枚举，定义形态在进化链中的阶段位置。
 * <p>
 * 配合 {@link PlayerFormGroup} 构成形态进化链。阶段决定了 CursedMoon 回退的行为、
 * 变形前进的目标位置、以及进度触发器的判定依据。
 * <p>
 * 除了枚举常量外，还提供一组 INDEX_* 常量用于与 {@link PlayerFormBase#FormIndex} 比较。
 */
public enum PlayerFormPhase {
	/**
	 * 未分类阶段，用于默认/过渡状态。不属于任何进化链。
	 */
    PHASE_CLEAR,
	/** 第一阶段形态。对应 {@link #INDEX_PHASE_0}。 */
	PHASE_0,
	/** 第二阶段形态。对应 {@link #INDEX_PHASE_1}。 */
	PHASE_1,
	/** 第三阶段形态。对应 {@link #INDEX_PHASE_2}。 */
	PHASE_2,
	/**
	 * 终极形态（永久形态阶段）。不受 CursedMoon 影响，无法被诅咒之月降级。
	 */
	PHASE_3,
	/** SP 特殊形态。仅有一个阶段，不受 CursedMoon 影响。通常用于隐藏或赞助者专属形态。 */
	PHASE_SP;

	// ===== 形态索引常量 =====

	/**
	 * 模组未激活（-2）。玩家尚未启用 SSC 功能时的占位索引。
	 */
	public static final int INDEX_PRE_ACTIVATE = -2;
	/**
	 * 原始幻形者（-1）。SSC 初始入口形态。
	 */
	public static final int INDEX_BASE_SHIFTER = -1;
	/**
	 * 第一阶段索引（0）。{@link #PHASE_0} 的数字表示。
	 */
	public static final int INDEX_PHASE_0 = 0;
	/**
	 * 第二阶段索引（1）。{@link #PHASE_1} 的数字表示。
	 */
	public static final int INDEX_PHASE_1 = 1;
	/**
	 * 第三阶段索引（2）。{@link #PHASE_2} 的数字表示。
	 */
	public static final int INDEX_PHASE_2 = 2;
	/**
	 * 终极形态索引（3）。{@link #PHASE_3} 的数字表示。
	 */
	public static final int INDEX_PHASE_3 = 3;
	/**
	 * SP 形态索引（5）。{@link #PHASE_SP} 的数字表示。
	 */
	public static final int INDEX_PHASE_SP = 5;
}
