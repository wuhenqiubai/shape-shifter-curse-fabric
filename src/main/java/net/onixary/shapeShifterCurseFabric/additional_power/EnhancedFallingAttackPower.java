package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnhancedFallingAttackPower extends PowerType {

    private final EntityAction targetActionOnHit;
    private final EntityAction selfActionOnHit;

    public static final TypedDataObjectFactory<EnhancedFallingAttackPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("target_action_on_critical_hit", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("self_action_on_critical_hit", EntityAction.DATA_TYPE.optional(), Optional.empty()),
                    (data, cond) -> new EnhancedFallingAttackPower(
                            data.get("target_action_on_critical_hit"),
                            data.get("self_action_on_critical_hit"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public EnhancedFallingAttackPower(Optional<EntityAction> targetActionOnHit, Optional<EntityAction> selfActionOnHit,
                                      Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.targetActionOnHit = targetActionOnHit.orElse(null);
        this.selfActionOnHit = selfActionOnHit.orElse(null);
    }

    public void executeTargetAction(Entity target) {
        if (targetActionOnHit != null) {
            targetActionOnHit.accept(target);
        }
    }

    public void executeSelfAction() {
        if (selfActionOnHit != null) {
            selfActionOnHit.accept(getHolder());
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("enhanced_falling_attack"));
    }

    public static PowerConfiguration<EnhancedFallingAttackPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}