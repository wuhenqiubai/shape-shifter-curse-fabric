package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.onixary.shapeShifterCurseFabric.additional_power.LootingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.SoulSpeedPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Unique
    private static int getLootingLevel(LivingEntity entity, int PreValue) {
        AtomicInteger powerLooting = new AtomicInteger(PreValue);
        PowerHolderComponent.getPowers(entity, LootingPower.class).forEach(power -> powerLooting.set(power.getLevel(powerLooting.get())));
        return powerLooting.get();
    }

    @Unique
    private static int getSoulSpeedLevel(LivingEntity entity, int PreValue) {
        AtomicInteger powerSoulSpeed = new AtomicInteger(PreValue);
        PowerHolderComponent.getPowers(entity, SoulSpeedPower.class).forEach(power -> powerSoulSpeed.set(power.getLevel(powerSoulSpeed.get())));
        return powerSoulSpeed.get();
    }

    @Inject(method = "getEquipmentLevel", at = @At("RETURN"), cancellable = true)
    private static void getEquipmentLevelMixin(RegistryEntry<Enchantment> enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (enchantment.matchesKey(Enchantments.LOOTING)) {
            cir.setReturnValue(getLootingLevel(entity, cir.getReturnValue()));
        } else if (enchantment.matchesKey(Enchantments.SOUL_SPEED)) {
            cir.setReturnValue(getSoulSpeedLevel(entity, cir.getReturnValue()));
        }
    }

    // hasSoulSpeed removed in 1.21 — soul speed is now data-driven via applyLocationBasedEffects.
    // getEquipmentLevel mixin already handles SoulSpeedPower contribution.

    // isPrimaryItem disabled: conflicts with ModernFix which optimizes getPossibleEntries
    // and may not call isPrimaryItem at all. TODO: re-evaluate if needed.
    /*
    @ModifyExpressionValue(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isPrimaryItem(Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean isPrimaryItem(boolean original, ItemStack stack, RegistryEntry<Enchantment> enchantment) {
        if (!original) {
            return EnchantmentUtils.isItemCanEnchantment(enchantment.getKey().orElseThrow(), stack);
        }
        return original;
    }
    */
}
