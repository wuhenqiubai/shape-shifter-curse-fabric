package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

public class SetFallingDistanceAction extends EntityActionType {

    public static final TypedDataObjectFactory<SetFallingDistanceAction> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("distance", SerializableDataTypes.FLOAT, 0.0f),
            data -> new SetFallingDistanceAction(
                    data.getFloat("distance")
            ),
            (actionType, serializableData) -> serializableData.instance()
                    .set("distance", actionType.distance)
    );

    private final float distance;

    public SetFallingDistanceAction(float distance) {
        this.distance = distance;
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();
        entity.fallDistance = this.distance;
        ShapeShifterCurseFabric.LOGGER.info("Set falling distance for entity {} to {}", entity.getName().getString(), this.distance);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return AdditionalEntityActions.SET_FALLING_DISTANCE;
    }

    public static ActionConfiguration<SetFallingDistanceAction> createConfig(Identifier id) {
        return ActionConfiguration.of(id, DATA_FACTORY);
    }
}
