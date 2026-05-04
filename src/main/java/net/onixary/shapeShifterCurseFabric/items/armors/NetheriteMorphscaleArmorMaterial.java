// TODO: 1.21.1 - ArmorMaterial interface changed significantly. Needs full rewrite with new record-based API.
/*
package net.onixary.shapeShifterCurseFabric.items.armors;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class NetheriteMorphscaleArmorMaterial implements ArmorMaterial {
    public static final NetheriteMorphscaleArmorMaterial INSTANCE = new NetheriteMorphscaleArmorMaterial();

    private static final int[] BASE_DURABILITY = new int[] {462, 555, 592, 481};
    private static final int[] PROTECTION_VALUES = new int[] {3, 6, 7, 3};

    @Override
    public int getDurability(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> BASE_DURABILITY[0];
            case LEGGINGS -> BASE_DURABILITY[1];
            case CHESTPLATE -> BASE_DURABILITY[2];
            case HELMET -> BASE_DURABILITY[3];
            default -> 0;
        };
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> PROTECTION_VALUES[0];
            case LEGGINGS -> PROTECTION_VALUES[1];
            case CHESTPLATE -> PROTECTION_VALUES[2];
            case BOOTS -> PROTECTION_VALUES[3];
            default -> 0;
        };
    }

    @Override
    public int getEnchantability() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.NETHERITE_SCRAP);
    }

    @Override
    public String getName() {
        return "netherite_morphscale";
    }

    @Override
    public float getToughness() {
        return 2.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1f;
    }
}
*/