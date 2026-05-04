package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.ItemConditionContext;
import io.github.apace100.apoli.condition.type.ItemConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AdditionalItemCondition {

    public static final String IS_MORPH_SCALE_ARMOR_TAG = "MorphScaleItem";

    public static final ConditionConfiguration<IsMorphScaleItemCondition> IS_MORPH_SCALE_ITEM_CONFIG =
            register(ShapeShifterCurseFabric.identifier("is_morph_scale_item"), IsMorphScaleItemCondition.DATA_FACTORY);

    public static final ConditionConfiguration<IsWeaponCondition> IS_WEAPON_CONFIG =
            register(ShapeShifterCurseFabric.identifier("is_weapon"), IsWeaponCondition.DATA_FACTORY);

    public static void register() {}

    @SuppressWarnings("unchecked")
    private static <T extends ItemConditionType> ConditionConfiguration<T> register(Identifier id, TypedDataObjectFactory<T> factory) {
        ConditionConfiguration<T> config = ConditionConfiguration.of(id, factory);
        Registry.register(ApoliRegistries.ITEM_CONDITION_TYPE, id,
                (ConditionConfiguration<ItemConditionType>) (Object) config);
        return config;
    }

    public static class IsWeaponCondition extends ItemConditionType {
        public static final TypedDataObjectFactory<IsWeaponCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData(),
                        data -> new IsWeaponCondition(),
                        (c, sd) -> sd.instance()
                );

        @Override
        public boolean test(ItemConditionContext ctx) {
            Collection<EntityAttributeModifier> modifiers = ctx.stack().getItem()
                    .getAttributeModifiers()
                    .modifiers().stream()
                    .filter(e -> e.attribute().equals(EntityAttributes.ATTACK_DAMAGE))
                    .flatMap(e -> e.modifiers().stream())
                    .toList();
            double totalAdd = 0;
            for (EntityAttributeModifier modifier : modifiers) {
                if (modifier.operation() == EntityAttributeModifier.Operation.ADD_VALUE) {
                    totalAdd += modifier.value();
                }
            }
            return totalAdd > 1;
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return IS_WEAPON_CONFIG;
        }
    }
}
