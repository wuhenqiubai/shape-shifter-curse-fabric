package net.onixary.shapeShifterCurseFabric.items.trinkets;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;

import java.util.List;

public class VenomSpindle extends AccessoryItem {
    public VenomSpindle(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.venom_spindle.tooltip").formatted(Formatting.YELLOW));
    }
}
