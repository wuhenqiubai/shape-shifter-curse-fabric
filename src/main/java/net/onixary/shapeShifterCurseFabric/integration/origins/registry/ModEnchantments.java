package net.onixary.shapeShifterCurseFabric.integration.origins.registry;

import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class ModEnchantments {

    //public static final Enchantment WATER_PROTECTION = new WaterProtectionEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});

    public static void register() {

        //register("water_protection", WATER_PROTECTION);
    }

    private static Enchantment register(String path, Enchantment enchantment) {
        Registry.register(Registries.ENCHANTMENT, Identifier.of(Origins.MODID, path), enchantment);
        return enchantment;
    }
}
