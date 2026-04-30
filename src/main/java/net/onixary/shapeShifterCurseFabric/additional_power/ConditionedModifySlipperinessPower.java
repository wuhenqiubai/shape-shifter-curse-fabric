package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class ConditionedModifySlipperinessPower extends PowerType {

    private final Predicate<CachedBlockPosition> predicate;
    private final EntityCondition condition;
    private final float slipperinessModifier;

    public static final TypedDataObjectFactory<ConditionedModifySlipperinessPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("block_condition", SerializableDataTypes.BLOCK_CONDITION, null)
                            .add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                            .add("modifier", SerializableDataTypes.FLOAT),
                    (data, cond) -> new ConditionedModifySlipperinessPower(
                            data.get("block_condition"),
                            data.get("entity_condition"),
                            data.getFloat("modifier"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public ConditionedModifySlipperinessPower(Predicate<CachedBlockPosition> predicate,
                                              Optional<EntityCondition> condition, float slipperinessModifier,
                                              Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.predicate = predicate;
        this.condition = condition.orElse(null);
        this.slipperinessModifier = slipperinessModifier;
    }

    public boolean doesApply(WorldView world, BlockPos pos) {
        CachedBlockPosition cbp = new CachedBlockPosition(world, pos, true);
        return predicate.test(cbp) && (condition == null || condition.test(getHolder()));
    }

    public float getSlipperinessModifier() {
        return slipperinessModifier;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("conditioned_modify_slipperiness"));
    }

    public static PowerConfiguration<ConditionedModifySlipperinessPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}