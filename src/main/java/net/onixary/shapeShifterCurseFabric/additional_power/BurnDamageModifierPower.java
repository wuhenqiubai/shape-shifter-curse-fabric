package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BurnDamageModifierPower extends PowerType {

    private final EntityAction action;
    private final float damageModifier;

    public static final TypedDataObjectFactory<BurnDamageModifierPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("modifier", SerializableDataTypes.FLOAT, 1.0f)
                            .add("action", EntityAction.DATA_TYPE.optional(), Optional.empty()),
                    (data, cond) -> new BurnDamageModifierPower(
                            data.get("action"),
                            data.getFloat("modifier"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public BurnDamageModifierPower(Optional<EntityAction> action, float damageModifier,
                                   Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.action = action.orElse(null);
        this.damageModifier = damageModifier;
    }

    public float getDamageModifier() {
        return damageModifier;
    }

    public void executeAction(Entity target) {
        if (action != null) {
            action.accept(new EntityActionContext(target, target.getPos()));
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("burn_damage_modifier"));
    }

    public static PowerConfiguration<BurnDamageModifierPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}