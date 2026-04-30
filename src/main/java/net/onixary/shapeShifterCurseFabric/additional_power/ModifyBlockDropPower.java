package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class ModifyBlockDropPower extends PowerType {
    private final Predicate<CachedBlockPosition> blockCondition;
    private final float chance;
    private final List<ItemStack> targetItemStack;

    public static final TypedDataObjectFactory<ModifyBlockDropPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("block_condition", io.github.apace100.apoli.data.ApoliDataTypes.BLOCK_CONDITION, null)
                            .add("chance", SerializableDataTypes.FLOAT, 0.0f)
                            .add("target_item_stack_list", SerializableDataTypes.ITEM_STACKS, null),
                    (data, condition) -> new ModifyBlockDropPower(
                            data.get("block_condition"),
                            data.getFloat("chance"),
                            Objects.requireNonNullElseGet(data.<List<ItemStack>>get("target_item_stack_list"), LinkedList::new),
                            condition),
                    (power, sd) -> sd.instance()
                            .set("block_condition", power.blockCondition)
                            .set("chance", power.chance)
                            .set("target_item_stack_list", power.targetItemStack)
            );

    public ModifyBlockDropPower(Predicate<CachedBlockPosition> blockCondition, float chance,
                                List<ItemStack> targetItemStack, Optional<EntityCondition> condition) {
        super(condition);
        this.blockCondition = blockCondition;
        this.chance = chance;
        this.targetItemStack = targetItemStack;
    }

    public boolean CanApply(CachedBlockPosition blockPosition) {
        if (this.blockCondition != null) {
            return this.blockCondition.test(blockPosition);
        }
        return true;
    }

    public @Nullable List<ItemStack> Apply(Random randomSource) {
        if (randomSource.nextFloat() < this.chance) {
            return this.targetItemStack;
        }
        return null;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("modify_block_drop"));
    }

    public static PowerConfiguration<ModifyBlockDropPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}