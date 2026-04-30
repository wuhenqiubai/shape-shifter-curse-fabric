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

public class WaterFlexibilityPower extends PowerType {
    public static final float MAX_FLEXIBILITY = 0.98F;

    private final float resistance;

    public static final TypedDataObjectFactory<WaterFlexibilityPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("water_flex", SerializableDataTypes.FLOAT, 0.5f),
                    (data, condition) -> new WaterFlexibilityPower(data.getFloat("water_flex"), condition),
                    (power, sd) -> sd.instance().set("water_flex", power.resistance)
            );

    public WaterFlexibilityPower(float resistance, Optional<EntityCondition> condition) {
        super(condition);
        this.resistance = Math.max(0.0f, Math.min(1.0f, resistance));
    }

    public float getResistance() {
        return resistance;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("water_flexibility"));
    }

    public static PowerConfiguration<WaterFlexibilityPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}