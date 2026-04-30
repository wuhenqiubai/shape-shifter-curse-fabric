package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CriticalDamageModifierPower extends PowerType {

    private final EntityAction action;
    private final float multiplier;

    public static final TypedDataObjectFactory<CriticalDamageModifierPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("multiplier", SerializableDataTypes.FLOAT, 1.5f),
                    (data, cond) -> new CriticalDamageModifierPower(
                            data.get("action"),
                            data.getFloat("multiplier"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public CriticalDamageModifierPower(Optional<EntityAction> action, float multiplier,
                                       Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.action = action.orElse(null);
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void executeAction() {
        if (action != null) {
            action.accept(getHolder());
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("critical_damage_modifier"));
    }

    public static PowerConfiguration<CriticalDamageModifierPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}