package net.onixary.shapeShifterCurseFabric.items.tools;

import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// 形态专属工具只应用耐久度逻辑，其他逻辑由形态Power+手持道具condition实现
public class DiamondMiningClaw extends PickaxeItem {

    public DiamondMiningClaw(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.diamond_mining_claw.tooltip").formatted(Formatting.YELLOW));
    }
}
