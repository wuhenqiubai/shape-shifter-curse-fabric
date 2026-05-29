package net.onixary.shapeShifterCurseFabric.items.accessory;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class AccessoryItem extends TrinketItem {
    static {
        System.err.println("[SSC-DEBUG-ERR] AccessoryItem class loaded!");
    }

    public enum DropRule {
        KEEP, DROP, DESTROY, DEFAULT
    }

    public record SlotData(Identifier slot, int index) {
    }

    public AccessoryItem(Settings settings) {
        super(settings);
        System.err.println("[SSC-DEBUG-ERR] AccessoryItem ctor: " + this.getClass().getSimpleName());
        this.accessoryInit(settings);
    }

    public void accessoryInit(Settings settings) {
    }

    public void accessoryTick(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
    }

    public void onEquip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        System.err.println("[SSC-DEBUG-ERR] Trinket EQUIP: " + stack.getItem() + " slot=" + slotData.slot() + "/" + slotData.index() + " player=" + accessoryOwner.getName().getString());
    }

    public void onUnequip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        System.err.println("[SSC-DEBUG-ERR] Trinket UNEQUIP: " + stack.getItem() + " slot=" + slotData.slot() + "/" + slotData.index() + " player=" + accessoryOwner.getName().getString());
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

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        var optComp = dev.emi.trinkets.api.TrinketsApi.getTrinketComponent(user);
        System.err.println("[SSC-DEBUG-ERR] Trinket use: item=" + stack.getItem() + " hasComponent=" + optComp.isPresent());
        if (optComp.isPresent()) {
            var comp = optComp.get();
            System.err.println("[SSC-DEBUG-ERR] Trinket component present, groups=" + comp.getInventory().size());
        }
        return super.use(world, user, hand);
    }
}
