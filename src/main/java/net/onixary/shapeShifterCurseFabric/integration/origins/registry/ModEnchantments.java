package net.onixary.shapeShifterCurseFabric.integration.origins.registry;

import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;

public class ModEnchantments {

    // Water Protection is now defined via JSON data file:
    //   data/origins/enchantment/water_protection.json
    // Enchantment is a final class in 1.21, must use data-driven registration.
    public static final RegistryKey<Enchantment> WATER_PROTECTION =
        RegistryKey.of(Registries.ENCHANTMENT.getKey(), Identifier.of(Origins.MODID, "water_protection"));

    public static void register() {
        // Enchantment registration is handled by JSON data files in 1.21.
        // No Java registration needed.
    }
}
