package net.onixary.shapeShifterCurseFabric.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import java.util.List;

// 酿造配方实际上不属于原版配方系统 但是都叫Recipe了 还是放到RecipePackage里吧
// FIXME: BrewingRecipeRegistry.Recipe is final in 1.21.1 — DynamicRecipe wrapper removed,
// targetForm tracking lost. Dynamic recipe reload needs rethinking.
public class BrewingRecipeUtils {
    private static final List<BrewingRecipeRegistry.Recipe<Potion>> DYNAMIC_POTION_RECIPES = Lists.newArrayList();
    private static final List<BrewingRecipeRegistry.Recipe<Item>> DYNAMIC_ITEM_RECIPES = Lists.newArrayList();

    public static void onLoadDynamicBrewingRecipesStart() {
        DYNAMIC_POTION_RECIPES.clear();
        DYNAMIC_ITEM_RECIPES.clear();
    }

    public static void onLoadDynamicBrewingRecipesEnd() {
        BrewingRecipeRegistry.POTION_RECIPES.addAll(DYNAMIC_POTION_RECIPES);
        BrewingRecipeRegistry.ITEM_RECIPES.addAll(DYNAMIC_ITEM_RECIPES);
    }

    /*
    ```json
    {
      "type": "potion",
      "input": "shape-shifter-curse:moondust_potion",
      "ingredient": "minecraft:bone",
      "output": "shape-shifter-curse:to_anubis_wolf_0_potion",
      "target_form": "example_namespace:example"
    }
    ```
    ```json
    {
      "type": "item",
      "input": "minecrafte:potion",
      "ingredient": "minecraft:gunpowder",
      "output": "minecraft:splash_potion"
    }
    ```
     */

    public static void registerPotionRecipe(JsonObject recipeJson) {
        if (recipeJson == null) {
            ShapeShifterCurseFabric.LOGGER.error("recipe json is null");
            return;
        }
        if (!recipeJson.has("type")) {
            ShapeShifterCurseFabric.LOGGER.error("recipe json has no type");
            return;
        }
        if (!recipeJson.has("input") || !recipeJson.has("ingredient") || !recipeJson.has("output")) {
            ShapeShifterCurseFabric.LOGGER.error("recipe json has no input or ingredient or output");
            return;
        }
        Identifier input = Identifier.tryParse(recipeJson.get("input").getAsString());
        Identifier ingredient = Identifier.tryParse(recipeJson.get("ingredient").getAsString());
        Identifier output = Identifier.tryParse(recipeJson.get("output").getAsString());
        if (input == null || ingredient == null || output == null) {
            ShapeShifterCurseFabric.LOGGER.error("recipe json has invalid input or ingredient or output");
            return;
        }
        Item ingredientItem = Registries.ITEM.get(ingredient);
        Ingredient ingredientObject = Ingredient.ofItems(new ItemConvertible[]{ingredientItem});
        switch (recipeJson.get("type").getAsString()) {
            case "potion" -> {
                Potion inputPotion = Registries.POTION.get(input);
                Potion outputPotion = Registries.POTION.get(output);
                DYNAMIC_POTION_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(inputPotion, ingredientObject, outputPotion));
            }
            case "item" -> {
                Item inputItem = Registries.ITEM.get(input);
                Item outputItem = Registries.ITEM.get(output);
                DYNAMIC_ITEM_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(inputItem, ingredientObject, outputItem));
            }
        }
        return;
    }
}
