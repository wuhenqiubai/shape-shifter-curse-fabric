package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.items.tools.DiamondMiningClaw;

import java.util.HashMap;
import java.util.HashSet;

public class EnchantmentUtils {
    public static  HashMap<RegistryKey<Enchantment>, HashSet<Class<? extends Item>>> enchantmentItemClassMap = new HashMap<>();
    public static HashMap<RegistryKey<Enchantment>, HashSet<Identifier>> enchantmentItemIDMap = new HashMap<>();

    static {
        registerEnchantmentItem(Enchantments.SHARPNESS, DiamondMiningClaw.class);
        registerEnchantmentItem(Enchantments.SMITE, DiamondMiningClaw.class);
        registerEnchantmentItem(Enchantments.BANE_OF_ARTHROPODS, DiamondMiningClaw.class);
        registerEnchantmentItem(Enchantments.FIRE_ASPECT, DiamondMiningClaw.class);
        registerEnchantmentItem(Enchantments.KNOCKBACK, DiamondMiningClaw.class);
        registerEnchantmentItem(Enchantments.LOOTING, DiamondMiningClaw.class);
    }

    public static void registerEnchantmentItem(RegistryKey<Enchantment> enchantment, Class<? extends Item> itemClass) {
        enchantmentItemClassMap.computeIfAbsent(enchantment, k -> new HashSet<>()).add(itemClass);
    }

    public static void registerEnchantmentItem(RegistryKey<Enchantment> enchantment, Identifier itemID) {
        enchantmentItemIDMap.computeIfAbsent(enchantment, k -> new HashSet<>()).add(itemID);
    }

    public static void registerEnchantmentItem(RegistryKey<Enchantment> enchantment, Item item) {
        enchantmentItemIDMap.computeIfAbsent(enchantment, k -> new HashSet<>()).add(Registries.ITEM.getId(item));
    }

    public static boolean isItemCanEnchantment(RegistryKey<Enchantment> enchantment, ItemStack itemStack) {
        if (enchantmentItemClassMap.containsKey(enchantment)) {
            for (Class<? extends Item> itemClass : enchantmentItemClassMap.get(enchantment)) {
                if (itemClass.isInstance(itemStack.getItem())) {
                    return true;
                }
            }
        }
        if (enchantmentItemIDMap.containsKey(enchantment)) {
            Identifier id = Registries.ITEM.getId(itemStack.getItem());
            for (Identifier itemID : enchantmentItemIDMap.get(enchantment)) {
                if (itemID.equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }
}
