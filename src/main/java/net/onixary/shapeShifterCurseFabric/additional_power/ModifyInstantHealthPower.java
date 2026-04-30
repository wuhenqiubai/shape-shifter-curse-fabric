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

public class ModifyInstantHealthPower extends PowerType {
    private final float MulScale;

    public static final TypedDataObjectFactory<ModifyInstantHealthPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("scale", SerializableDataTypes.FLOAT, 1.0f),
                    (data, condition) -> new ModifyInstantHealthPower(data.getFloat("scale"), condition),
                    (power, sd) -> sd.instance().set("scale", power.MulScale)
            );

    public ModifyInstantHealthPower(float MulScale, Optional<EntityCondition> condition) {
        super(condition);
        this.MulScale = MulScale;
    }

    public float ApplyMulScale(float orig_value) {
        return orig_value * MulScale;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("modify_instant_health_scale"));
    }

    public static PowerConfiguration<ModifyInstantHealthPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}