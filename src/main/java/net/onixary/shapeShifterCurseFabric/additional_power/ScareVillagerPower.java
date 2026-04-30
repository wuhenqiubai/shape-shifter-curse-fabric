package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ScareVillagerPower extends PowerType {

    public static final TypedDataObjectFactory<ScareVillagerPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData(),
                    (data, condition) -> new ScareVillagerPower(condition),
                    (power, sd) -> sd.instance()
            );

    public ScareVillagerPower(Optional<EntityCondition> condition) { super(condition); }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("scare_villager"));
    }

    public static PowerConfiguration<ScareVillagerPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}