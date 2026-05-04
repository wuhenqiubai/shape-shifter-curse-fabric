package net.onixary.shapeShifterCurseFabric.integration.origins.registry;

// ModLoot: loot table integration disabled for 1.21 port.
// - OriginLootCondition needs LootCondition API migration (JsonSerializer → MapCodec)
// - EnchantmentLevelEntry / EnchantedBookItem.forEnchantment API changed in 1.21
// - SetNbtLootFunction replaced by SetComponentsLootFunction in 1.21
// Water Protection enchantment books can be added to loot tables via JSON data files:
//   data/origins/loot_table/...
//
// See: https://docs.fabricmc.net/1.21.1/develop/data-generation/loot-tables

public class ModLoot {
    public static void registerLootTables() {
        // Disabled for 1.21 port
    }
}
