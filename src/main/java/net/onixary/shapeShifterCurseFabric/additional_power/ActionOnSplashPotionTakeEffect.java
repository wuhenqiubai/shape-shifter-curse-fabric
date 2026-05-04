package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionOnSplashPotionTakeEffect extends PowerType {

    private final EntityAction entityAction;
    private final EntityCondition entityCondition;
    private final boolean triggerOnNoEffect;

    public static final TypedDataObjectFactory<ActionOnSplashPotionTakeEffect> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                            .add("trigger_on_no_effect", SerializableDataTypes.BOOLEAN, false),
                    (data, cond) -> new ActionOnSplashPotionTakeEffect(
                            data.get("entity_action"),
                            data.get("entity_condition"),
                            data.get("trigger_on_no_effect"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public ActionOnSplashPotionTakeEffect(Optional<EntityAction> entityAction, Optional<EntityCondition> entityCondition,
                                          boolean triggerOnNoEffect, Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.entityAction = entityAction.orElse(null);
        this.entityCondition = entityCondition.orElse(null);
        this.triggerOnNoEffect = triggerOnNoEffect;
    }

    public void executeAction() {
        LivingEntity entity = getHolder();
        if (entity instanceof PlayerEntity player) {
            if (entityCondition == null || entityCondition.test(player)) {
                if (entityAction != null) {
                    entityAction.accept(new EntityActionContext(player));
                }
            }
        }
    }

    public boolean shouldTriggerOnNoEffect() {
        return triggerOnNoEffect;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("action_on_splash_potion_take_effect"));
    }

    public static PowerConfiguration<ActionOnSplashPotionTakeEffect> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}