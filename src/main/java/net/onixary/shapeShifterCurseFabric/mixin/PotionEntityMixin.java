package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public class PotionEntityMixin {
    @Inject(method = "applySplashPotion", at = @At("HEAD"))
    public void applySplashPotion(Iterable<StatusEffectInstance> statusEffects, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            PotionEntity realThis = ((PotionEntity) (Object) this);
            ItemStack stack = realThis.getStack();
            var customData = stack.get(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
            if (customData != null) {
                Identifier CTPFormID = CTPUtils.getCTPFormIDFromNBT(customData.copyNbt());
                if (CTPFormID != null) {
                    CTPUtils.setTransformativePotionForm(player, CTPFormID);
                }
            }
        }
    }

    @Inject(method = "applyLingeringPotion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    public void applyLingeringPotion(PotionContentsComponent potion, CallbackInfo ci, @Local AreaEffectCloudEntity areaEffectCloudEntity) {
        PotionEntity realThis = ((PotionEntity) (Object) this);
        ItemStack stack = realThis.getStack();
        var customData = stack.get(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            Identifier CTPFormID = CTPUtils.getCTPFormIDFromNBT(customData.copyNbt());
            if (CTPFormID != null && areaEffectCloudEntity instanceof CTPUtils.CTPFormIDHolder) {
                ((CTPUtils.CTPFormIDHolder) areaEffectCloudEntity).setCTPFormID(CTPFormID);
            }
        }
    }

}
