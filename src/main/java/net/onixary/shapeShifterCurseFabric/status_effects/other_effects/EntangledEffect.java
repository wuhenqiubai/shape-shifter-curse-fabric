package net.onixary.shapeShifterCurseFabric.status_effects.other_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.onixary.shapeShifterCurseFabric.status_effects.EntangledEffectUtils;
import net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects;

public class EntangledEffect extends StatusEffect {
    public EntangledEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 1;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        RegistryEntry<StatusEffect> entangled = Registries.STATUS_EFFECT.getEntry(RegOtherStatusEffects.ENTANGLED_EFFECT);
        StatusEffectInstance instance = entity.getStatusEffect(entangled);
        if (instance != null) {
            int NowDuration = instance.getDuration();
            int CurrentLevel = instance.getAmplifier();
            int TargetLevel = NowDuration / EntangledEffectUtils.ENTANGLED_DURATION_PER_LEVEL;
            if (CurrentLevel != TargetLevel) {
                entity.removeStatusEffect(entangled);
                entity.addStatusEffect(new StatusEffectInstance(entangled, NowDuration, TargetLevel));
            }
        }
        return true;
    }
}
