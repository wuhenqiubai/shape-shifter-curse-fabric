package net.onixary.shapeShifterCurseFabric.items.armors;

import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MorphScaleArmor extends ArmorItem {
    public MorphScaleArmor(Type type) {
        super(MorphscaleArmorMaterial.INSTANCE, type, new Settings().maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.morphscale_armor.tooltip").formatted(Formatting.YELLOW));
    }
}
