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

public class FallingProtectionPower extends PowerType {

    private final float fallDistance;

    public static final TypedDataObjectFactory<FallingProtectionPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("fall_distance", SerializableDataTypes.FLOAT),
                    (data, condition) -> new FallingProtectionPower(data.getFloat("fall_distance"), condition),
                    (power, sd) -> sd.instance().set("fall_distance", power.fallDistance)
            );

    public FallingProtectionPower(float fallDistance, Optional<EntityCondition> condition) {
        super(condition);
        this.fallDistance = fallDistance;
    }

    public float getFallDistance() {
        return fallDistance;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("falling_protection"));
    }

    public static PowerConfiguration<FallingProtectionPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}