package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.RestrictArmorPower;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.slot.ArmorSlot")
public abstract class ArmorSlotMixin {
    @Shadow @Final private LivingEntity entity;

    @Shadow @Final private EquipmentSlot equipmentSlot;

    @Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
    private void preventRestrictedArmorInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            PowerHolderComponent component = PowerHolderComponent.KEY.get(this.entity);
            if (component.getPowers(RestrictArmorPower.class).stream()
                    .anyMatch(rap -> !rap.canEquip(stack, this.equipmentSlot))) {
                cir.setReturnValue(false);
            }
        }
    }
}
