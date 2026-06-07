package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

/**
 * 全局静态参数配置。调整这些值会影响诅咒之月、本能、变形特效等核心系统。
 * <p>
 * 注意：此类的常量在编译时硬编码，修改后需要重新构建。
 * 如果需要动态配置，请考虑使用 {@link net.onixary.shapeShifterCurseFabric.config.CommonConfig} 或 {@link net.onixary.shapeShifterCurseFabric.config.ClientConfig}。
 */
public class StaticParams {
    private StaticParams() {
    }
	// ===== 诅咒之月系统 =====
	/**
	 * 诅咒之月最小间隔天数
	 */
    public static final int CURSED_MOON_INTERVAL_DAY = 5;
	/** 基础触发概率（期望约 1720 秒 ≈ 3 天） */
    public static final float CURSED_MOON_BASE_PROBABILITY = 0.0001f;
	/** 每 tick 概率递增 */
    public static final float CURSED_MOON_PROBABILITY_INCREASE = 0.00000042f;
	/** 概率上限 */
    public static final float CURSED_MOON_PROBABILITY_MAX = 0.05f;
	// ===== 变形效果 =====
	/**
	 * 变形效果的默认持续时间（400 秒）
	 */
	public static final int T_EFFECT_DEFAULT_DURATION = 400 * 20;
	// ===== 物品相关 =====
	/** 月之尘掉落概率 */
    public static final float MOONDUST_DROP_PROBABILITY = 0.45F;
    /** 熟悉诅咒药水掉落概率 */
    public static final float FAMILIAR_CURSE_POTION_DROP_PROBABILITY = 0.35F;
	// ===== 本能系统 =====
    /** 本能上限 */
    public static final float INSTINCT_MAX = 100.0f;
	/** 每 tick 本能增加速率（阶段 0） */
    public static final float INSTINCT_INCREASE_RATE_0 = (INSTINCT_MAX / 9000.0f) / 20.0f;
	/** 每 tick 本能增加速率（阶段 1） */
    public static final float INSTINCT_INCREASE_RATE_1 = (INSTINCT_MAX / 9000.0f) / 20.0f;
	// ===== 特效设置 =====
	/**
	 * 变形入场特效持续时间（3 秒）
	 */
	public static final int TRANSFORM_FX_DURATION_IN = 3 * 20;
	/**
	 * 变形退场特效持续时间（5 秒）
	 */
	public static final int TRANSFORM_FX_DURATION_OUT = 5 * 20;
    /** 变形粒子效果 */
    public static final ParticleEffect PLAYER_TRANSFORM_PARTICLE = ParticleTypes.ENCHANT;
    // ===== 变形生物设置 =====
    /** 变形生物默认攻击伤害 */
    public static final float CUSTOM_MOB_DEFAULT_DAMAGE = 0.5F;
    /** 非主动变形生物的攻击范围 */
    public static final double CUSTOM_MOB_DEFAULT_ATTACK_RANGE = 3.0;
    /** 变形生物默认发射粒子 */
    public static final ParticleEffect CUSTOM_MOB_DEFAULT_PARTICLE = ParticleTypes.ENCHANT;
}
