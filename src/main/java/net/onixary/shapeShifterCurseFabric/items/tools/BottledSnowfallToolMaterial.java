package net.onixary.shapeShifterCurseFabric.items.tools;

import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class BottledSnowfallToolMaterial implements ToolMaterial {
    public static final BottledSnowfallToolMaterial INSTANCE = new BottledSnowfallToolMaterial();

    @Override
    public int getDurability() { return 250; }

    @Override
    public float getMiningSpeedMultiplier() { return 0f; }

    @Override
    public float getAttackDamage() { return 1; }

    @Override
    public int getEnchantability() { return 0; }

    @Override
    public TagKey<Block> getInverseTag() { return BlockTags.INCORRECT_FOR_WOODEN_TOOL; }

    @Override
    public Ingredient getRepairIngredient() { return Ingredient.ofItems(Items.DIAMOND); }
}
