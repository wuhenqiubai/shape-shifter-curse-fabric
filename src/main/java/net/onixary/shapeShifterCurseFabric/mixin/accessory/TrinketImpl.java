package net.onixary.shapeShifterCurseFabric.mixin.accessory;

import dev.emi.trinkets.api.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

@Mixin(AccessoryItem.class)
public abstract class TrinketImpl {
    @Unique
    private static final HashMap<Integer, AccessoryItem.SlotData> slotDataCache = new HashMap<>();

    @Unique
    private AccessoryItem.SlotData getSlotData(SlotReference slot) {
        SlotType slotType = slot.inventory().getSlotType();
        return slotDataCache.computeIfAbsent(slot.hashCode(), k ->
            new AccessoryItem.SlotData(Identifier.of("trinket", "%s/%s".formatted(slotType.getGroup(), slotType.getName())), slot.index()));
    }

    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).accessoryTick(stack, entity, getSlotData(slot));
    }

    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).onEquip(stack, entity, getSlotData(slot));
    }

    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).onUnequip(stack, entity, getSlotData(slot));
    }

    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return ((AccessoryItem) (Object) this).canEquip(stack, entity, getSlotData(slot));
    }

    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return ((AccessoryItem) (Object) this).canUnequip(stack, entity, getSlotData(slot));
    }

    public void onBreak(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((AccessoryItem) (Object) this).onBreak(stack, entity, getSlotData(slot));
    }

    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        AccessoryItem.DropRule dropRule = ((AccessoryItem) (Object) this).getDropRule(stack, entity, getSlotData(slot));
        return switch (dropRule) {
            case KEEP -> TrinketEnums.DropRule.KEEP;
            case DROP -> TrinketEnums.DropRule.DROP;
            case DESTROY -> TrinketEnums.DropRule.DESTROY;
            case DEFAULT -> TrinketEnums.DropRule.DEFAULT;
        };
    }
}
