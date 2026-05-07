package net.onixary.shapeShifterCurseFabric.integration.origins.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.util.OriginLootCondition;

// Enchantment loot tables can be added via JSON data files:
//   data/origins/loot_table/...

public class ModLoot {

    public static void registerLootTables() {
        Registry.register(Registries.LOOT_CONDITION_TYPE, Origins.identifier("origin"), OriginLootCondition.TYPE);
    }
}
