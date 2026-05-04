package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoonDustMatrix  extends Item {
    public MoonDustMatrix(Settings settings) {
        super(settings.maxCount(64));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.moondust_matrix.tooltip").formatted(Formatting.GRAY));
    }
}