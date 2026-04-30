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

public class AlwaysSprintSwimmingPower extends PowerType {

    private final float hungerMultiplier;

    public static final TypedDataObjectFactory<AlwaysSprintSwimmingPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("hunger_multiplier", SerializableDataTypes.FLOAT, 1.0f),
                    (data, condition) -> new AlwaysSprintSwimmingPower(data.getFloat("hunger_multiplier"), condition),
                    (power, sd) -> sd.instance().set("hunger_multiplier", power.hungerMultiplier)
            );

    public AlwaysSprintSwimmingPower(float hungerMultiplier, Optional<EntityCondition> condition) {
        super(condition);
        this.hungerMultiplier = hungerMultiplier;
    }

    public float getHungerMultiplier() {
        return hungerMultiplier;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("always_sprint_swimming"));
    }

    public static PowerConfiguration<AlwaysSprintSwimmingPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}