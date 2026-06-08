package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

/**
 * SSC 自定义标签注册表。定义实体、物品、方块的自定义 Tag。
 * <p>
 * 标签在 JSON 数据包中通过 {@code tags/} 目录定义，此处仅声明 TagKey 常量。
 *
 * @see TagKey
 */
public class ModTags {
	/**
	 * 被标记为灾厄村民的实体（用于乌鸦/蝙蝠等形态的敌对检测）
	 */
    public static final TagKey<EntityType<?>> Illager_Tag = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ShapeShifterCurseFabric.MOD_ID, "illager"));
	/** 被标记为女巫的实体 */
    public static final TagKey<EntityType<?>> Witch_Tag = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ShapeShifterCurseFabric.MOD_ID, "witch"));
	/** 被标记为蜘蛛的实体 */
    public static final TagKey<EntityType<?>> Spider_Tag = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ShapeShifterCurseFabric.MOD_ID, "spider"));
	/** 可改变形态大小的物品（用于蜘蛛等形态的大小调整） */
    public static final TagKey<Item> MorphScaleItem_Tag = TagKey.of(RegistryKeys.ITEM, Identifier.of(ShapeShifterCurseFabric.MOD_ID, "morph_scale_item"));
	/** 被标记为肉类的物品（来自 Origins 模组） */
    public static final TagKey<Item> Meat_Tag = TagKey.of(RegistryKeys.ITEM, Identifier.of("origins", "meat"));
	/** 类似脚手架的方块（蜘蛛形态可攀爬） */
    public static final TagKey<Block> LIKE_SCAFFOLDING_TAG = TagKey.of(RegistryKeys.BLOCK, Identifier.of(ShapeShifterCurseFabric.MOD_ID, "like_scaffolding"));
	/** 类似蜘蛛网的方块（减缓移动速度） */
    public static final TagKey<Block> LIKE_COBWEB_TAG = TagKey.of(RegistryKeys.BLOCK, Identifier.of(ShapeShifterCurseFabric.MOD_ID, "like_cobweb"));
	/** 蜘蛛流体茧的黑名单实体类型（不被茧捕获） */
    public static final TagKey<EntityType<?>> SPIDER_FLUID_COCOON_BLACKLIST = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ShapeShifterCurseFabric.MOD_ID, "spider_fluid_cocoon_blacklist"));
}
