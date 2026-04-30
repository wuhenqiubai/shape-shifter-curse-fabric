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

public class ModifyPotionStackPower extends PowerType {

    private final int count;

    public static final TypedDataObjectFactory<ModifyPotionStackPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData().add("count", SerializableDataTypes.INT, 1),
                    (data, condition) -> new ModifyPotionStackPower(data.getInt("count"), condition),
                    (power, sd) -> sd.instance().set("count", power.count)
            );

    public ModifyPotionStackPower(int count, Optional<EntityCondition> condition) {
        super(condition);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("modify_potion_stack"));
    }

    public static PowerConfiguration<ModifyPotionStackPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}