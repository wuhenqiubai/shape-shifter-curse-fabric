package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import org.jetbrains.annotations.Nullable;

// 这个Instance仅出现在服务器端 客户端为StatusEffectInstance
public class TransformativeStatusInstance extends StatusEffectInstance {

    public TransformativeStatusInstance(BaseTransformativeStatusEffect effect, int duration) {
        super(Registries.STATUS_EFFECT.getEntry(effect), duration, 0, false, false, true);
    }

    @Override
    public boolean update(LivingEntity entity, Runnable overwriteCallback) {
        if (entity instanceof ServerPlayerEntity player && this.getDuration() <= 1) {
            ShapeShifterCurseFabric.ON_TRANSFORM_EFFECT_FADE.trigger(player);
        }
        return super.update(entity, overwriteCallback);
    }

    public void ActiveEffect(ServerPlayerEntity player) {
        BaseTransformativeStatusEffect effect = this.getTransformativeEffectType();
        if (effect != null) {
            effect.ActiveEffect(player);
        }
    }

    public static @Nullable TransformativeStatusInstance formStatusEffectInstance(StatusEffectInstance instance) {
        RegistryEntry<StatusEffect> entry = instance.getEffectType();
        StatusEffect effect = entry.value();
        if (effect instanceof BaseTransformativeStatusEffect baseTransformativeStatusEffect) {
            return new TransformativeStatusInstance(baseTransformativeStatusEffect, instance.getDuration());
        }
        return null;
    }

    public @Nullable BaseTransformativeStatusEffect getTransformativeEffectType() {
        if (super.getEffectType().value() instanceof BaseTransformativeStatusEffect baseTransformativeStatusEffect) {
            return baseTransformativeStatusEffect;
        }
        return null;
    }
}
