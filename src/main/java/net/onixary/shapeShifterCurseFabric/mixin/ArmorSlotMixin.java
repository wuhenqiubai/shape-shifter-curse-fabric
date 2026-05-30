package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.RestrictArmorPower;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.onixary.shapeShifterCurseFabric.util.ModTags;
import net.onixary.shapeShifterCurseFabric.util.MorphScaleTagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.slot.ArmorSlot")
public abstract class ArmorSlotMixin {
    private static final String MSI_TAG = "MorphScaleItem";

    @Shadow @Final private LivingEntity entity;
    @Shadow @Final private EquipmentSlot equipmentSlot;

    @Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
    private void preventRestrictedArmorInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;
        if (isMorphScaleItem(stack)) return;
        for (RestrictArmorPower rap : PowerHolderComponent.KEY.get(this.entity).getPowers(RestrictArmorPower.class)) {
            if (!rap.canEquip(stack, this.equipmentSlot)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    private static boolean isMorphScaleItem(ItemStack stack) {
        if (stack.isIn(ModTags.MorphScaleItem_Tag)) return true;
        if (MorphScaleTagLoader.getMorphScaleItems().contains(Registries.ITEM.getId(stack.getItem()).toString())) return true;
        var c = stack.get(DataComponentTypes.CUSTOM_DATA);
        return c != null && c.copyNbt().getBoolean(MSI_TAG);
    }
}
