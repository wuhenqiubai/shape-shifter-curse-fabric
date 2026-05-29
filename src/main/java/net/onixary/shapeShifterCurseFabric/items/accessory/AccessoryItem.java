package net.onixary.shapeShifterCurseFabric.items.accessory;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public abstract class AccessoryItem extends TrinketItem {
	public enum DropRule {
        KEEP, DROP, DESTROY, DEFAULT
    }

	public record SlotData(Identifier slot, int index) {
	}

    public AccessoryItem(Settings settings) {
        super(settings);
        this.accessoryInit(settings);
    }

    public void accessoryInit(Settings settings) {
        return;
    }

    public void accessoryTick(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        return;
    }

    public void onEquip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        return;
    }

    public void onUnequip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        return;
    }

    public boolean canEquip(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return true;
    }

    public boolean canUnequip(ItemStack stack, LivingEntity entity, SlotData slotData) {
        // hasBindingCurse signature changed in 1.21 - needs RegistryWrapper.WrapperLookup
        return true;
    }

    public void onBreak(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return;
    }

    public DropRule getDropRule(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return DropRule.DEFAULT;
    }
}
