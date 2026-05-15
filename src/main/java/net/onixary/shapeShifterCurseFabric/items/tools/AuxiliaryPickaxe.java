package net.onixary.shapeShifterCurseFabric.items.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;


public class AuxiliaryPickaxe extends PickaxeItem {

    public AuxiliaryPickaxe(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.auxiliary_pickaxe.tooltip").formatted(Formatting.YELLOW));
    }
}
