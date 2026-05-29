package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.RestrictArmorPower;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(targets = "net.minecraft.screen.slot.ArmorSlot")
public abstract class ArmorSlotMixin {
    private static final Set<String> MORPH_SCALE_ITEMS = Set.of(
        "shape-shifter-curse:morphscale_headring", "shape-shifter-curse:morphscale_vest",
        "shape-shifter-curse:morphscale_cuish", "shape-shifter-curse:morphscale_anklet",
        "shape-shifter-curse:netherite_morphscale_headring", "shape-shifter-curse:netherite_morphscale_vest",
        "shape-shifter-curse:netherite_morphscale_cuish", "shape-shifter-curse:netherite_morphscale_anklet",
        "shape-shifter-curse:diamond_mining_claw"
    );
    private static final String MSI_TAG = "MorphScaleItem";

    @Shadow @Final private LivingEntity entity;

    @Shadow @Final private EquipmentSlot equipmentSlot;

    @Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
    private void preventRestrictedArmorInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;
        PowerHolderComponent component = PowerHolderComponent.KEY.get(this.entity);
        var powers = component.getPowers(RestrictArmorPower.class);
        if (powers.isEmpty()) return;
        String itemId = Registries.ITEM.getId(stack.getItem()).toString();
        boolean isMorphItem = MORPH_SCALE_ITEMS.contains(itemId)
            || hasMorphScaleTag(stack);
        if (isMorphItem) return; // always allow morph-scale items
        for (RestrictArmorPower rap : powers) {
            if (!rap.canEquip(stack, this.equipmentSlot)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    private static boolean hasMorphScaleTag(ItemStack stack) {
        var customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        return customData != null && customData.copyNbt().getBoolean(MSI_TAG);
    }
}
