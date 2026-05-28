package net.onixary.shapeShifterCurseFabric.recipes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.*;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class UpgradeRecipe implements SmithingRecipe, ISmithingRecipeEX {
    public final Identifier id;
    public final Predicate<ItemStack> template;
    public final Predicate<ItemStack> base;
    public final Predicate<ItemStack> addition;
    public final Function<ItemStack, ItemStack> upgradeResult;

    public boolean isUpgradeAll() {
        return false;
    }

    public UpgradeRecipe(Identifier id, Predicate<ItemStack> template, Predicate<ItemStack> base, Predicate<ItemStack> addition, Function<ItemStack, ItemStack> upgradeResult) {
        this.id = id;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.upgradeResult = upgradeResult;
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return this.template.test(input.template()) && this.base.test(input.base()) && this.addition.test(input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack itemStack = input.base();
        if (this.base.test(itemStack)) {
            ItemStack outputStack = itemStack.copy();
            if (!isUpgradeAll()) {
                outputStack.setCount(1);
            }
            return this.upgradeResult.apply(outputStack);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
        return this.upgradeResult.apply(itemStack.copy());
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean overrideVanillaOnTakeOutput() {
        return this.isUpgradeAll();
    }

    @Override
    public void onTakeOutput(SmithingScreenHandler screenHandler, PlayerEntity player, ItemStack stack) {
        if (this.isUpgradeAll()) {
            screenHandler.decrementStack(0);
            screenHandler.input.setStack(1, ItemStack.EMPTY);
            screenHandler.decrementStack(2);
        }
    }
}
