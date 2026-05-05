package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class EntangledEffectUtils {
    public static final int ENTANGLED_DURATION_PER_LEVEL = 20 * 5;
    public static final int ENTANGLED_MAX_LEVEL = 4;
    public static final int ENTANGLED_FULL_DURATION = 20 * 15;
    // PVP考虑，对玩家裹茧效果的时长缩短
    public static final int ENTANGLED_FULL_DURATION_PLAYER = 20 * 5;

    public static void applyEntangledEffect(LivingEntity target, int Time) {
        // 1.21: Convert StatusEffect to RegistryEntry<StatusEffect>
        RegistryEntry<net.minecraft.entity.effect.StatusEffect> entangledEffectType = 
            net.minecraft.registry.Registries.STATUS_EFFECT.getEntry(RegOtherStatusEffects.ENTANGLED_EFFECT);
        RegistryEntry<net.minecraft.entity.effect.StatusEffect> entangledFullType = 
            net.minecraft.registry.Registries.STATUS_EFFECT.getEntry(RegOtherStatusEffects.ENTANGLED_FULL_EFFECT);
        
        if (target.getStatusEffect(entangledFullType) != null) {
            return;
        }
        StatusEffectInstance entangledEffect = target.getStatusEffect(entangledEffectType);
        if (entangledEffect == null) {
            target.addStatusEffect(new StatusEffectInstance(entangledEffectType, Time, Time / ENTANGLED_DURATION_PER_LEVEL));
        } else {
            int newDuration = entangledEffect.getDuration() + Time;
            int newAmplifier = Math.min(entangledEffect.getAmplifier() + 1, ENTANGLED_MAX_LEVEL);
            target.removeStatusEffect(entangledEffectType);
            target.addStatusEffect(new StatusEffectInstance(entangledEffectType, newDuration, newAmplifier));
        }
        entangledEffect = target.getStatusEffect(entangledEffectType);
        if (entangledEffect != null) {
            int NowDuration = entangledEffect.getDuration();
            if (NowDuration >= ENTANGLED_DURATION_PER_LEVEL * (ENTANGLED_MAX_LEVEL + 1)) {
                target.removeStatusEffect(entangledEffectType);
                if(target instanceof PlayerEntity){
                    target.addStatusEffect(new StatusEffectInstance(entangledFullType, ENTANGLED_FULL_DURATION_PLAYER, 0));
                }
                else{
                    target.addStatusEffect(new StatusEffectInstance(entangledFullType, ENTANGLED_FULL_DURATION, 0));
                }
            }
        }
    }
}
