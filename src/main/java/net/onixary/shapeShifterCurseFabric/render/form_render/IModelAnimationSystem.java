package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import mod.azure.azurelib.common.internal.common.cache.object.GeoBone;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * 模型动画系统接口。定义 SSC 渲染管道中 GeoModel 骨骼动画的处理流程。
 * <p>
 * 标准的渲染流程分三个阶段：
 * <ol>
 *   <li>{@link #beforeRender} — 预处理（重置骨骼、设置初始值）</li>
 *   <li>{@link #processAnimation} — 核心动画映射（PAL 骨骼 → GeoModel 骨骼）</li>
 *   <li>{@link #afterRender} — 后处理（程序化动画如尾巴、翅膀）</li>
 * </ol>
 * <p>
 * 第一人称手臂渲染有独立的 {@code *FirstPerson} 变体。
 * 默认实现 {@link DefaultModelAnimationSystem} 提供了标准的行为。
 *
 * @see DefaultModelAnimationSystem
 * @see FormModel
 * @see FormRenderer
 */
public interface IModelAnimationSystem {
	/**
	 * 从 JSON 配置加载动画系统参数。
	 *
	 * @param json 配置数据，null 表示使用默认配置
	 */
	void loadConfig(@Nullable JsonObject json);

	/**
	 * 预处理钩子：在 processAnimation 之前调用，用于重置骨骼变换。
	 *
	 * @param formRenderer      形态渲染器
	 * @param model             形态模型
	 * @param renderer          玩家实体渲染器
	 * @param player            目标玩家
	 * @param limbAngle         肢体角度
	 * @param limbDistance      肢体距离
	 * @param tickDelta         帧间插值系数
	 * @param animationProgress 动画进度
	 * @param headYaw           头部偏航
	 * @param headPitch         头部俯仰
	 */
	default void beforeRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
	}

	/**
	 * 核心动画处理：将 PAL 骨骼变换映射到 GeoModel 骨骼。
	 * 所有形态的动画逻辑在此步骤生效。
	 *
	 * @param formRenderer      形态渲染器
	 * @param model             形态模型
	 * @param renderer          玩家实体渲染器
	 * @param player            目标玩家
	 * @param limbAngle         肢体角度
	 * @param limbDistance      肢体距离
	 * @param tickDelta         帧间插值系数
	 * @param animationProgress 动画进度
	 * @param headYaw           头部偏航
	 * @param headPitch         头部俯仰
	 */
	void processAnimation(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch);

	/**
	 * 后处理钩子：在 processAnimation 之后调用，用于程序化动画（尾巴物理、翅膀动画等）。
	 *
	 * @param formRenderer      形态渲染器
	 * @param model             形态模型
	 * @param renderer          玩家实体渲染器
	 * @param player            目标玩家
	 * @param limbAngle         肢体角度
	 * @param limbDistance      肢体距离
	 * @param tickDelta         帧间插值系数
	 * @param animationProgress 动画进度
	 * @param headYaw           头部偏航
	 * @param headPitch         头部俯仰
	 */
	default void afterRender(FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
	}

	/**
	 * 第一人称手臂渲染预处理。
	 *
	 * @param geoBone      当前手臂骨骼
	 * @param formRenderer 形态渲染器
	 * @param model        形态模型
	 * @param renderer     玩家实体渲染器
	 * @param player       目标玩家
	 * @param arm          手臂 ModelPart
	 * @param sleeve       袖子 ModelPart
	 * @return 处理后的手臂骨骼
	 */
	default @Nullable GeoBone beforeRenderFirstPerson(@Nullable GeoBone geoBone, FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, ModelPart arm, ModelPart sleeve) {
		return geoBone;
	}

	/**
	 * 第一人称手臂渲染动画处理。
	 *
	 * @param geoBone      当前手臂骨骼
	 * @param formRenderer 形态渲染器
	 * @param model        形态模型
	 * @param renderer     玩家实体渲染器
	 * @param player       目标玩家
	 * @param arm          手臂 ModelPart
	 * @param sleeve       袖子 ModelPart
	 * @return 动画处理后的手臂骨骼
	 */
	@Nullable GeoBone processAnimationFirstPerson(@Nullable GeoBone geoBone, FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, ModelPart arm, ModelPart sleeve);

	/**
	 * 第一人称手臂渲染后处理。
	 *
	 * @param geoBone      当前手臂骨骼
	 * @param formRenderer 形态渲染器
	 * @param model        形态模型
	 * @param renderer     玩家实体渲染器
	 * @param player       目标玩家
	 * @param arm          手臂 ModelPart
	 * @param sleeve       袖子 ModelPart
	 * @return 后处理后的手臂骨骼
	 */
	default @Nullable GeoBone afterRenderFirstPerson(@Nullable GeoBone geoBone, FormRenderer formRenderer, FormModel model, PlayerEntityRenderer renderer, PlayerEntity player, ModelPart arm, ModelPart sleeve) {
		return geoBone;
	}

}
