package net.onixary.shapeShifterCurseFabric.util;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.additional_power.CustomEdiblePower;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CustomEdibleUtils {

    public static HashMap<UUID, HashMap<Identifier, FoodComponent>> customEdibleMap = new HashMap<>();

    public static FoodComponent getPowerFoodComponent(PlayerEntity user, ItemStack itemStack) {
        if (user == null || itemStack == null || itemStack.isEmpty()) {
            return null;
        }
        HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.computeIfAbsent(user.getUuid(), k -> new HashMap<>());
        return customEdible.getOrDefault(Registries.ITEM.getId(itemStack.getItem()), null);
    }

    public static void addCustomEdible(PlayerEntity user, Identifier itemId, FoodComponent foodComponent) {
        HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.computeIfAbsent(user.getUuid(), k -> new HashMap<>());
        customEdible.put(itemId, foodComponent);
    }

    public static void addCustomEdibleWithList(PlayerEntity user, List<Identifier> itemIdList, FoodComponent foodComponent) {
        HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.computeIfAbsent(user.getUuid(), k -> new HashMap<>());
        for (Identifier itemId : itemIdList) {
            customEdible.put(itemId, foodComponent);
        }
    }

    public static void clearCustomEdible(PlayerEntity user, Identifier itemId) {
        if (customEdibleMap.containsKey(user.getUuid())) {
            customEdibleMap.get(user.getUuid()).remove(itemId);
        }
    }

    public static void clearCustomEdibleWithList(PlayerEntity user, List<Identifier> itemIdList) {
        if (customEdibleMap.containsKey(user.getUuid())) {
            HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.get(user.getUuid());
            for (Identifier itemId : itemIdList) {
                customEdible.remove(itemId);
            }
        }
    }

    public static void ReloadPlayerCustomEdible(PlayerEntity user) {
        try {
            customEdibleMap.computeIfAbsent(user.getUuid(), k -> new HashMap<>()).clear();
            HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.get(user.getUuid());
            PowerHolderComponent.getPowers(user, CustomEdiblePower.class).forEach(
                    customEdiblePower -> {
                        for (Identifier itemId : customEdiblePower.getItemIdList()) {
                            customEdible.put(itemId, customEdiblePower.getFoodComponent());
                        }
                    }
            );
        } catch (Exception e) {
            // ShapeShifterCurseFabric.LOGGER.error("Reload Player Custom Edible Failed", e);
        }
    }

}
