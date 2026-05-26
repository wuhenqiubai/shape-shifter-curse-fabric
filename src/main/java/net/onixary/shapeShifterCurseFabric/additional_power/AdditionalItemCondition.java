package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class AdditionalItemCondition {
    public static void register() {
        register(IsMorphScaleItemCondition.getFactory1());
        register(IsMorphScaleItemCondition.getFactory2());
        register(new ConditionFactory<ItemStack>(
                ShapeShifterCurseFabric.identifier("is_weapon"),
                new SerializableData(),
                (data, itemstack) -> {
                    Collection<EntityAttributeModifier> modifiers = itemstack.getItem().getAttributeModifiers(itemstack, EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    double totalAdd = 0;
                    for (EntityAttributeModifier modifier : modifiers) {
                        if (modifier.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                            totalAdd += modifier.getValue();
                        }
                    }
                    return false;
                }
        ));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void register(ConditionFactory<Pair<World, ItemStack>> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), (ConditionFactory) conditionFactory);
    }
}
