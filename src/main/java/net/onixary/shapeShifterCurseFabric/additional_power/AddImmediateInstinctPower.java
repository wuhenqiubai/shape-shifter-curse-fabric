package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.applyImmediateEffect;

public class AddImmediateInstinctPower extends PowerType {

    private final String instinctEffectID;
    private final float value;

    public static final TypedDataObjectFactory<AddImmediateInstinctPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("instinct_effect_id", SerializableDataTypes.STRING)
                            .add("value", SerializableDataTypes.FLOAT, 0.0f),
                    (data, condition) -> new AddImmediateInstinctPower(condition, data),
                    (power, sd) -> sd.instance()
            );

    public AddImmediateInstinctPower(Optional<EntityCondition> condition, SerializableData.Instance data) {
        super(condition);
        this.value = data.getFloat("value");

        if (!data.isPresent("instinct_effect_id")) {
            ShapeShifterCurseFabric.LOGGER.error("Instinct effect ID is missing");
            this.instinctEffectID = null;
            return;
        }

        this.instinctEffectID = data.getString("instinct_effect_id");

        LivingEntity entity = getHolder();
        applyImmediateEffect((ServerPlayerEntity) entity, this.instinctEffectID, this.value);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("add_immediate_instinct"));
    }

    public static PowerConfiguration<AddImmediateInstinctPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}