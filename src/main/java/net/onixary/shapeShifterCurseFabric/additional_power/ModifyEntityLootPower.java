package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.condition.ItemCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class ModifyEntityLootPower extends PowerType {
    private final Predicate<ItemStack> FromItemCondition;
    private final float chance;
    private final Item targetItem;
    private final ItemStack targetItemStack;

    public static final TypedDataObjectFactory<ModifyEntityLootPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("from_item_condition", ItemCondition.DATA_TYPE, null)
                            .add("chance", SerializableDataTypes.FLOAT, 0.0f)
                            .add("target_item", SerializableDataTypes.ITEM, null)
                            .add("target_item_stack", SerializableDataTypes.ITEM_STACK, null),
                    (data, condition) -> new ModifyEntityLootPower(
                            data.get("from_item_condition"),
                            data.getFloat("chance"),
                            data.<Item>get("target_item"),
                            data.<ItemStack>get("target_item_stack"),
                            condition),
                    (power, sd) -> sd.instance()
                            .set("from_item_condition", power.FromItemCondition)
                            .set("chance", power.chance)
                            .set("target_item", power.targetItem)
                            .set("target_item_stack", power.targetItemStack)
            );

    public ModifyEntityLootPower(Predicate<ItemStack> FromItemCondition, float chance,
                                  Item targetItem, ItemStack targetItemStack,
                                  Optional<EntityCondition> condition) {
        super(condition);
        this.FromItemCondition = FromItemCondition;
        this.chance = chance;
        this.targetItem = targetItem;
        this.targetItemStack = targetItemStack;
    }

    public ItemStack ApplyModifyDrop(ItemStack itemStack, Random randomSource) {
        if (!FromItemCondition.test(itemStack)) {
            return itemStack;
        }
        if (randomSource.nextFloat() < this.chance) {
            if (targetItem != null) {
                ItemStack finalItemStack = new ItemStack(targetItem, itemStack.getCount());
                finalItemStack.applyComponentsFrom(itemStack.getComponents());
                return finalItemStack;
            } else if (targetItemStack != null) {
                return targetItemStack.copy();
            }
        }
        return itemStack;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("modify_entity_loot"));
    }

    public static PowerConfiguration<ModifyEntityLootPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}