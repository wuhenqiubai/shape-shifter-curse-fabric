package net.onixary.shapeShifterCurseFabric.player_form.effect;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerTransformEffectManager {

    public static void applyStartTransformEffect(ServerPlayerEntity player, int duration) {
        // add darkness effect
        StatusEffectInstance darknessEffect = new StatusEffectInstance(StatusEffects.BLINDNESS, duration);
        player.addStatusEffect(darknessEffect);

        // add immobility effect
        StatusEffectInstance immobilityEffect = new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 245);
        player.addStatusEffect(immobilityEffect);

        // prevent jump
        StatusEffectInstance preventJumpEffect = new StatusEffectInstance(StatusEffects.JUMP_BOOST, duration, 250);  // 250级在床上不飞天
        player.addStatusEffect(preventJumpEffect);

    }

    public static void applyEndTransformEffect(ServerPlayerEntity player, int duration) {
        // add nausea effect
        StatusEffectInstance nauseaEffect = new StatusEffectInstance(StatusEffects.NAUSEA, duration);
        player.addStatusEffect(nauseaEffect);

        // add immobility effect
        StatusEffectInstance immobilityEffect = new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 245);
        player.addStatusEffect(immobilityEffect);

        // prevent jump
        StatusEffectInstance preventJumpEffect = new StatusEffectInstance(StatusEffects.JUMP_BOOST, duration, 250);  // 250级在床上不飞天
        player.addStatusEffect(preventJumpEffect);

    }

    public static void applyFinaleTransformEffect(ServerPlayerEntity player, int duration){

        // slowness effect remain some time
        StatusEffectInstance immobilityEffect = new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 200);
        player.addStatusEffect(immobilityEffect);

    }
}
