package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 玩家 Power 动画控制器接口。
 * <p>
 * 通过 Mixin 注入到 {@link net.minecraft.entity.player.PlayerEntity}，所有方法使用 {@code shape_shifter_curse$} 前缀以遵循 Fabric Mixin 命名约定。
 * <p>
 * 支持三种 Power 动画播放模式：限次播放、限时播放、循环播放。
 * Power 动画与 FSM 动画不同——Power 动画由数据包/外部触发，FSM 动画由玩家状态自动驱动。
 *
 * @see AnimSystem
 * @see AnimUtils#playPowerAnimWithTime
 * @see AnimUtils#playPowerAnimWithCount
 */
public interface IPlayerAnimController {
	/**
	 * @return 当前播放的 Power 动画 ID，无播放中的动画则返回 null
	 */
    @Nullable Identifier shape_shifter_curse$getPowerAnimationID();

	/** @return 当前 Power 动画已播放的次数 */
    int shape_shifter_curse$getPowerAnimationCount();

	/** @return 当前 Power 动画已播放的时间（tick） */
    int shape_shifter_curse$getPowerAnimationTime();

	/**
	 * 播放指定次数的 Power 动画。
	 *
	 * @param id        Power 动画 ID
	 * @param PlayCount 播放次数
     */
    void shape_shifter_curse$playAnimationWithCount(@NotNull Identifier id, int PlayCount);

	/**
	 * 播放指定时长的 Power 动画。
	 *
	 * @param id   Power 动画 ID
	 * @param Time 播放时长（tick）
     */
    void shape_shifter_curse$playAnimationWithTime(@NotNull Identifier id, int Time);

	/**
	 * 循环播放 Power 动画，直到调用 {@link #shape_shifter_curse$stopAnimation()}。
	 *
	 * @param id Power 动画 ID
     */
    void shape_shifter_curse$playAnimationLoop(@NotNull Identifier id);

	/** 停止当前正在播放的 Power 动画。 */
    void shape_shifter_curse$stopAnimation();

	/**
	 * Power 动画播放完成后的回调。
	 *
	 * @param id 已完成的 Power 动画 ID
     */
    void shape_shifter_curse$animationDoneCallBack(@NotNull Identifier id);

	/**
	 * 直接设置动画数据（跳过播放接口）。
	 *
	 * @param id    Power 动画 ID（null 表示无）
	 * @param count 播放次数
	 * @param time  播放时间（tick）
     */
    void shape_shifter_curse$setAnimationData(@Nullable Identifier id, int count, int time);

}
