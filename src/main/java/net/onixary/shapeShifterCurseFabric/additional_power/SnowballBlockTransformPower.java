package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SnowballBlockTransformPower extends PowerType {

    public static final TypedDataObjectFactory<SnowballBlockTransformPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData(),
                    (data, cond) -> new SnowballBlockTransformPower(cond),
                    (power, sd) -> sd.instance()
            );

    public SnowballBlockTransformPower(Optional<EntityCondition> powerCondition) {
        super(powerCondition);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("snowball_block_transform"));
    }

    public static PowerConfiguration<SnowballBlockTransformPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}