package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class AdditionalItemCondition {
    public static void register() {
        register(IsMorphScaleItemCondition.getFactory());
        register(new ConditionFactory<>(
                ShapeShifterCurseFabric.identifier("is_weapon"),
                new SerializableData(),
                (data, itemStack) -> {
                    AttributeModifiersComponent modifiers = itemStack.getItem().getAttributeModifiers();
                    for (var entry : modifiers.modifiers()) {
                        if (entry.attribute() == EntityAttributes.GENERIC_ATTACK_DAMAGE
                            || entry.attribute() == EntityAttributes.GENERIC_ATTACK_SPEED) {
                            return true;
                        }
                    }
                    return false;
                }
        ));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void register(ConditionFactory<ItemStack> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), (ConditionFactory) conditionFactory);
    }
}
