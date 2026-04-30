package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SlowdownPercentPower extends PowerType {
    public float Multiplier = 1.0f;

    public static final TypedDataObjectFactory<SlowdownPercentPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("multiplier", SerializableDataTypes.FLOAT, 1.0f),
                    (data, condition) -> new SlowdownPercentPower(data.getFloat("multiplier"), condition),
                    (power, sd) -> sd.instance().set("multiplier", power.Multiplier)
            );

    public SlowdownPercentPower(float multiplier, Optional<EntityCondition> condition) {
        super(condition);
        this.Multiplier = multiplier;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("slowdown_percent"));
    }

    public static PowerConfiguration<SlowdownPercentPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}