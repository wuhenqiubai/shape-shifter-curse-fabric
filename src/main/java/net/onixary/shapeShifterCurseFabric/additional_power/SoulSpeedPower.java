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

public class SoulSpeedPower extends PowerType {
    private final int level;
    private final int maxLevel;

    public static final TypedDataObjectFactory<SoulSpeedPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("level", SerializableDataTypes.INT, 1)
                            .add("max_level", SerializableDataTypes.INT, Integer.MAX_VALUE),
                    (data, condition) -> new SoulSpeedPower(data.getInt("level"), data.getInt("max_level"), condition),
                    (power, sd) -> sd.instance()
                            .set("level", power.level)
                            .set("max_level", power.maxLevel)
            );

    public SoulSpeedPower(int level, int maxLevel, Optional<EntityCondition> condition) {
        super(condition);
        this.level = level;
        this.maxLevel = maxLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getLevel(int preProcessLevel) {
        return Math.min(preProcessLevel + level, maxLevel);
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("soul_speed"));
    }

    public static PowerConfiguration<SoulSpeedPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}