package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowEntity.class)
public class ArrowEntityMixin {
    @Unique
    private boolean IsCTPArrow = false;

    @Unique
    private Identifier CTPFormID = null;

    @Inject(method = "setStack", at = @At("HEAD"))
    public void setStack(ItemStack stack, CallbackInfo ci) {
        var customData = stack.get(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
        if (customData == null) return;
        Identifier CTPFormID = CTPUtils.getCTPFormIDFromNBT(customData.copyNbt());
        if (CTPFormID != null) {
            IsCTPArrow = true;
            this.CTPFormID = CTPFormID;
        }
    }

    @Inject(method = "onHit", at = @At("HEAD"))
    public void onHit(LivingEntity target, CallbackInfo ci) {
        if (IsCTPArrow && target instanceof PlayerEntity player) {
            CTPUtils.setTransformativePotionForm(player, CTPFormID);
        }
    }

    @Inject(method = "getDefaultItemStack", at = @At("RETURN"))
    public void getDefaultItemStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (IsCTPArrow && stack.getItem().equals(Items.TIPPED_ARROW)) {
            var customData = stack.get(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
            if (customData != null) {
                CTPUtils.setCTPFormIDToNBT(customData.copyNbt(), CTPFormID);
            }
        }
    }
}
