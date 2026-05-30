package net.onixary.shapeShifterCurseFabric.items.accessory;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public abstract class AccessoryItem extends Item {
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
    }

    public void accessoryTick(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
    }

    public void onEquip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
    }

    public void onUnequip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
    }

    public boolean canEquip(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return true;
    }

    public boolean canUnequip(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return true;
    }

    public void onBreak(ItemStack stack, LivingEntity entity, SlotData slotData) {
    }

    public DropRule getDropRule(ItemStack stack, LivingEntity entity, SlotData slotData) {
        return DropRule.DEFAULT;
    }
}