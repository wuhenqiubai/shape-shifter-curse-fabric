package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChanceCondition extends EntityConditionType {

    public static final TypedDataObjectFactory<ChanceCondition> DATA_FACTORY =
            TypedDataObjectFactory.simple(
                    new SerializableData().add("chance", SerializableDataTypes.FLOAT),
                    data -> new ChanceCondition(data.getFloat("chance")),
                    (c, sd) -> sd.instance()
            );

    private final float chance;

    public ChanceCondition(float chance) { this.chance = chance; }

    @Override
    public boolean test(EntityConditionContext ctx) {
        return new Random().nextFloat() < chance;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("chance"), DATA_FACTORY);
    }
}