package net.onixary.shapeShifterCurseFabric.integration.origins.power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.ActionOnCallbackPowerType;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OriginsCallbackPower extends PowerType {

    public static final TypedDataObjectFactory<OriginsCallbackPower> DATA_FACTORY = PowerType.createConditionedDataFactory(
        new SerializableData()
            .add("entity_action_respawned", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("entity_action_removed", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("entity_action_gained", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("entity_action_lost", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("entity_action_added", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("entity_action_chosen", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("execute_chosen_when_orb", SerializableDataTypes.BOOLEAN, true),
        (data, condition) -> new OriginsCallbackPower(
            data.get("entity_action_respawned"),
            data.get("entity_action_removed"),
            data.get("entity_action_gained"),
            data.get("entity_action_lost"),
            data.get("entity_action_added"),
            data.get("entity_action_chosen"),
            data.getBoolean("execute_chosen_when_orb"),
            condition
        ),
        (powerType, serializableData) -> serializableData.instance()
            .set("entity_action_respawned", powerType.entityActionRespawned)
            .set("entity_action_removed", powerType.entityActionRemoved)
            .set("entity_action_gained", powerType.entityActionGained)
            .set("entity_action_lost", powerType.entityActionLost)
            .set("entity_action_added", powerType.entityActionAdded)
            .set("entity_action_chosen", powerType.entityActionChosen)
            .set("execute_chosen_when_orb", powerType.executeChosenWhenOrb)
    );

    private final Optional<EntityAction> entityActionRespawned;
    private final Optional<EntityAction> entityActionRemoved;
    private final Optional<EntityAction> entityActionGained;
    private final Optional<EntityAction> entityActionLost;
    private final Optional<EntityAction> entityActionAdded;
    private final Optional<EntityAction> entityActionChosen;
    private final boolean executeChosenWhenOrb;

    public OriginsCallbackPower(Optional<EntityAction> entityActionRespawned,
                                 Optional<EntityAction> entityActionRemoved,
                                 Optional<EntityAction> entityActionGained,
                                 Optional<EntityAction> entityActionLost,
                                 Optional<EntityAction> entityActionAdded,
                                 Optional<EntityAction> entityActionChosen,
                                 boolean executeChosenWhenOrb,
                                 Optional<EntityCondition> condition) {
        super(condition);
        this.entityActionRespawned = entityActionRespawned;
        this.entityActionRemoved = entityActionRemoved;
        this.entityActionGained = entityActionGained;
        this.entityActionLost = entityActionLost;
        this.entityActionAdded = entityActionAdded;
        this.entityActionChosen = entityActionChosen;
        this.executeChosenWhenOrb = executeChosenWhenOrb;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return OriginsPowerTypes.ACTION_ON_CALLBACK;
    }

    @Override
    public void onRespawn() {
        entityActionRespawned
            .filter(action -> this.isActive())
            .ifPresent(action -> action.execute(getHolder()));
    }

    @Override
    public void onGained() {
        entityActionGained
            .filter(action -> this.isActive())
            .ifPresent(action -> action.execute(getHolder()));
    }

    @Override
    public void onRemoved() {
        entityActionRemoved
            .filter(action -> this.isActive())
            .ifPresent(action -> action.execute(getHolder()));
    }

    @Override
    public void onLost() {
        entityActionLost
            .filter(action -> this.isActive())
            .ifPresent(action -> action.execute(getHolder()));
    }

    @Override
    public void onAdded() {
        entityActionAdded
            .filter(action -> this.isActive())
            .ifPresent(action -> action.execute(getHolder()));
    }

    public void onChosen(boolean isOrbOfOrigins) {
        if (!isOrbOfOrigins || executeChosenWhenOrb) {
            entityActionChosen
                .filter(action -> this.isActive())
                .ifPresent(action -> action.execute(getHolder()));
        }
    }
}
