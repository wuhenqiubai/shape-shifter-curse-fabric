package net.onixary.shapeShifterCurseFabric.integration.origins.registry;

import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.util.OriginLootCondition;

// ModLoot: loot table integration disabled for 1.21 port.
// - OriginLootCondition needs LootCondition API migration (JsonSerializer → MapCodec)
// - EnchantmentLevelEntry / EnchantedBookItem.forEnchantment API changed in 1.21
// - SetNbtLootFunction replaced by SetComponentsLootFunction in 1.21
// Water Protection enchantment books can be added to loot tables via JSON data files:
//   data/origins/loot_table/...
//
// See: https://docs.fabricmc.net/1.21.1/develop/data-generation/loot-tables


public class ModLoot {

    /* //没用到的常量
    private static final Identifier DUNGEON_LOOT = Identifier.of("minecraft", "chests/simple_dungeon");
    private static final Identifier STRONGHOLD_LIBRARY = Identifier.of("minecraft", "chests/stronghold_library");
    private static final Identifier MINESHAFT = Identifier.of("minecraft", "chests/abandoned_mineshaft");
    private static final Identifier WATER_RUIN = Identifier.of("minecraft", "chests/underwater_ruin_small");
     */

    public static final LootConditionType ORIGIN_LOOT_CONDITION = registerLootCondition("origin", new OriginLootCondition.Serializer());

    private static LootConditionType registerLootCondition(String path, OriginLootCondition.Serializer serializer) {
        return Registry.register(Registries.LOOT_CONDITION_TYPE, Origins.identifier(path), new LootConditionType(serializer));
    }

    public static void registerLootTables() {
        // 如果需要，得看commit 7322f336
    }

    /* // 只有registerLootTables用了，先注释掉
    private static NbtCompound createEnchantmentTag(Enchantment enchantment, int level) {
        EnchantmentLevelEntry entry = new EnchantmentLevelEntry(enchantment, level);
        return EnchantedBookItem.forEnchantment(entry).getNbt();
    }
     */
}
