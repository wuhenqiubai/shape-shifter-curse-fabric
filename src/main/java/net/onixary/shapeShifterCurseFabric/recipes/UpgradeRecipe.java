package net.onixary.shapeShifterCurseFabric.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class UpgradeRecipe implements Recipe<SmithingRecipeInput> {
    private final Identifier id;
    private final Predicate<ItemStack> template;
    private final Predicate<ItemStack> base;
    private final Predicate<ItemStack> addition;
    private final Function<ItemStack, ItemStack> upgradeResult;

    public UpgradeRecipe(Identifier id, Predicate<ItemStack> template, Predicate<ItemStack> base, Predicate<ItemStack> addition, Function<ItemStack, ItemStack> upgradeResult) {
        this.id = id;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.upgradeResult = upgradeResult;
    }

    public boolean testTemplate(ItemStack stack) {
        return this.template.test(stack);
    }

    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

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
            outputStack.setCount(1);
            return this.upgradeResult.apply(outputStack);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
        return this.upgradeResult.apply(itemStack.copy());
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }
}
