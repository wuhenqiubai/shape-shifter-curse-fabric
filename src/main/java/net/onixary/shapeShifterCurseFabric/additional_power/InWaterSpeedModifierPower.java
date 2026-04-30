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

public class InWaterSpeedModifierPower extends PowerType {
    private final float Modifier;

    public static final TypedDataObjectFactory<InWaterSpeedModifierPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("modifier", SerializableDataTypes.FLOAT, 1.0f),
                    (data, condition) -> new InWaterSpeedModifierPower(data.getFloat("modifier"), condition),
                    (power, sd) -> sd.instance().set("modifier", power.Modifier)
            );

    public InWaterSpeedModifierPower(float Modifier, Optional<EntityCondition> condition) {
        super(condition);
        this.Modifier = Modifier;
    }

    public float getSpeedModifier() {
        return Modifier;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("in_water_speed_modifier"));
    }

    public static PowerConfiguration<InWaterSpeedModifierPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}