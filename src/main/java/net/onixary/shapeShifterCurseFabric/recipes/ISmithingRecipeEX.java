package net.onixary.shapeShifterCurseFabric.recipes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SmithingScreenHandler;

public interface ISmithingRecipeEX {
    boolean overrideVanillaOnTakeOutput();

    void onTakeOutput(SmithingScreenHandler screenHandler, PlayerEntity player, ItemStack stack);
}
