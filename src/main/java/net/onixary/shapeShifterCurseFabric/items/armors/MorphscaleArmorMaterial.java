package net.onixary.shapeShifterCurseFabric.items.armors;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MorphscaleArmorMaterial {
    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
        Map.of(
            ArmorItem.Type.HELMET, 2,
            ArmorItem.Type.CHESTPLATE, 6,
            ArmorItem.Type.LEGGINGS, 7,
            ArmorItem.Type.BOOTS, 2
        ),
        10,
        SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
        () -> Ingredient.ofItems(Items.DIAMOND),
        List.of(new ArmorMaterial.Layer(Identifier.of(ShapeShifterCurseFabric.MOD_ID, "morphscale"))),
        1.0F,
        0.0F
    );

    public static RegistryEntry<ArmorMaterial> ENTRY;

    public static void initialize() {
        ENTRY = Registry.registerReference(Registries.ARMOR_MATERIAL,
            Identifier.of(ShapeShifterCurseFabric.MOD_ID, "morphscale"), INSTANCE);
    }
}
