package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class AdditionalItemCondition {
    public static void register() {
        register(IsMorphScaleItemCondition.getFactory());
        register(new ConditionFactory<ItemStack>(
                ShapeShifterCurseFabric.identifier("is_weapon"),
                new SerializableData(),
                (data, itemstack) -> {
                    // TODO: getAttributeModifiers API changed in 1.21
                    return false;
                }
        ));
    }

    // TODO: Registry.register type mismatch in Apoli 2.12.0
    // ApoliRegistries.ITEM_CONDITION is Registry<ConditionFactory<Pair<World,ItemStack>>>
    // but conditionFactory is ConditionFactory<ItemStack> - type params don't match
    private static void register(ConditionFactory<ItemStack> conditionFactory) {
        // Registry.register(ApoliRegistries.ITEM_CONDITION,
        //     RegistryKey.of(ApoliRegistries.ITEM_CONDITION.getKey(), conditionFactory.getSerializerId()),
        //     conditionFactory);
    }
}
