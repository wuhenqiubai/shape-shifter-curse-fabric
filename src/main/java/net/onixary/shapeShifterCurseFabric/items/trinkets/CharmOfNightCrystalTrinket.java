package net.onixary.shapeShifterCurseFabric.items.trinkets;

import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CharmOfNightCrystalTrinket extends AccessoryItem {
    public CharmOfNightCrystalTrinket(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.charm_of_night_crystal.tooltip").formatted(Formatting.YELLOW));
    }
}
