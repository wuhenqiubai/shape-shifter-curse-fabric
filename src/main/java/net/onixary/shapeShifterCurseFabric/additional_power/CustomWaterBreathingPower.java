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

public class CustomWaterBreathingPower extends PowerType {

    public static final TypedDataObjectFactory<CustomWaterBreathingPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("land_water_breathing_level", SerializableDataTypes.INT, 24)
                            .add("damage_when_no_air", SerializableDataTypes.BOOLEAN, false),
                    (data, condition) -> new CustomWaterBreathingPower(
                            data.getInt("land_water_breathing_level"),
                            data.getBoolean("damage_when_no_air"),
                            condition),
                    (power, sd) -> sd.instance()
                            .set("land_water_breathing_level", power.landWaterBreathLevel)
                            .set("damage_when_no_air", power.damageWhenNoAir)
            );

    private final int landWaterBreathLevel;
    private final boolean damageWhenNoAir;

    public CustomWaterBreathingPower(int landWaterBreathLevel, boolean damageWhenNoAir, Optional<EntityCondition> condition) {
        super(condition);
        this.landWaterBreathLevel = landWaterBreathLevel;
        this.damageWhenNoAir = damageWhenNoAir;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("custom_water_breathing"));
    }

    public static PowerConfiguration<CustomWaterBreathingPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }

    public int getLandWaterBreathLevel() { return landWaterBreathLevel; }
    public boolean isDamage_when_no_air() { return damageWhenNoAir; }
}