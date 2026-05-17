package net.onixary.shapeShifterCurseFabric.player_animation;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

// fetch from Animation_Overhaul mod
// https://github.com/nvb-uy/Animation_Overhaul/blob/main/src/main/java/elocindev/animation_overhaul/api/AnimationHolder.java
public class AnimationHolder {
    public static AnimationHolder EMPTY = new AnimationHolder((KeyframeAnimation) null, false);

    private float speed;
    private int fade;
    private boolean isEnabled;
    @Nullable private KeyframeAnimation animation;

    public AnimationHolder(Identifier animation_id, boolean isEnabled, float speed) {
        this(getAnimationSafe(animation_id), isEnabled, speed, 5);
    }

    public AnimationHolder(Identifier animation_id, boolean isEnabled) {
        this(getAnimationSafe(animation_id), isEnabled, 1.0f, 2);
    }

    public AnimationHolder(Identifier animation_id, boolean isEnabled, float speed, int fade) {
        this(getAnimationSafe(animation_id), isEnabled, speed, fade);
    }

    private static KeyframeAnimation getAnimationSafe(Identifier animation_id) {
        try {
            // 在信雅互联兼容层下，需要特殊处理
            if (FabricLoader.getInstance().isModLoaded("connector")) {
                // 尝试通过反射调用正确的方法
                try {
                    // NeoForge 版本使用 ResourceLocation
                    Class<?> resourceLocationClass = Class.forName("net.minecraft.resources.ResourceLocation");
                    Object resourceLocation = resourceLocationClass.getMethod("parse", String.class)
                            .invoke(null, animation_id.toString());

                    java.lang.reflect.Method method = PlayerAnimationRegistry.class.getMethod("getAnimation", resourceLocationClass);
                    return (KeyframeAnimation) method.invoke(null, resourceLocation);
                } catch (Exception e) {
                    ShapeShifterCurseFabric.LOGGER.warn("Failed to load animation via reflection for {}: {}", animation_id, e.getMessage());
                    return null;
                }
            } else {
                // Fabric 原生环境直接使用 Identifier
                return PlayerAnimationRegistry.getAnimation(animation_id);
            }
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Error loading animation {}: {}", animation_id, e.getMessage());
            return null;
        }
    }

    public AnimationHolder(@Nullable KeyframeAnimation animation, boolean isEnabled, float speed, int fade) {
        this.isEnabled = isEnabled;
        this.animation = animation;
        this.speed = speed;
        this.fade = fade;
    }

    public AnimationHolder(@Nullable KeyframeAnimation animation, boolean isEnabled) {
        this(animation, isEnabled, 1.0f, 2);
    }

    public AnimationHolder() { this.isEnabled = false; this.animation = null; }

    @Nullable
    public KeyframeAnimation getAnimation() {
        if (!isEnabled) return null;

        return animation;
    }

    public AnimationHolder setAnimation(KeyframeAnimation animation) {
        this.animation = animation;

        return this;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public AnimationHolder setEnabled(boolean condition) {
        this.isEnabled = condition;

        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public AnimationHolder setSpeed(float speed) {
        this.speed = speed;

        return this;
    }

    public int getFade() {
        return fade;
    }

    public AnimationHolder setFade(int fade) {
        this.fade = fade;

        return this;
    }
}