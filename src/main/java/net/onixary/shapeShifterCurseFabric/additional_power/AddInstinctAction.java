package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.applyEffect;

public class AddInstinctAction extends EntityActionType {

    public static final TypedDataObjectFactory<AddInstinctAction> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("instinct_effect_id", SerializableDataTypes.STRING)
                    .add("value", SerializableDataTypes.FLOAT, 0.0f)
                    .add("duration", SerializableDataTypes.INT, 1),
            data -> new AddInstinctAction(
                    data.getString("instinct_effect_id"),
                    data.getFloat("value"),
                    data.getInt("duration")
            ),
            (action, serializableData) -> serializableData.instance()
                    .set("instinct_effect_id", action.instinctEffectId)
                    .set("value", action.value)
                    .set("duration", action.duration)
    );

    private final String instinctEffectId;
    private final float value;
    private final int duration;

    public AddInstinctAction(String instinctEffectId, float value, int duration) {
        this.instinctEffectId = instinctEffectId;
        this.value = value;
        this.duration = duration;
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();
        if (!(entity instanceof ServerPlayerEntity playerEntity)) {
            return;
        }
        applyEffect(playerEntity, instinctEffectId, value, duration);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return AdditionalEntityActions.ADD_INSTINCT;
    }

    public static ActionConfiguration<AddInstinctAction> createConfig(Identifier id) {
        return ActionConfiguration.of(id, DATA_FACTORY);
    }
}