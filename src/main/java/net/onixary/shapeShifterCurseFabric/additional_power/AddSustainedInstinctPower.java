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

import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.applySustainedEffect;

public class AddSustainedInstinctPower extends PowerType {

    private final String instinctEffectID;
    private final float value;
    private final int duration;

    public static final TypedDataObjectFactory<AddSustainedInstinctPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("instinct_effect_id", SerializableDataTypes.STRING)
                            .add("value", SerializableDataTypes.FLOAT, 0.0f)
                            .add("duration", SerializableDataTypes.INT, 1),
                    (data, condition) -> new AddSustainedInstinctPower(condition, data),
                    (power, sd) -> sd.instance()
            );

    public AddSustainedInstinctPower(Optional<EntityCondition> condition, SerializableData.Instance data) {
        super(condition);

        if (!data.isPresent("instinct_effect_id")) {
            ShapeShifterCurseFabric.LOGGER.error("Instinct effect ID is missing");
            this.instinctEffectID = null;
            this.value = 0.0f;
            this.duration = 0;
            return;
        }

        this.instinctEffectID = data.getString("instinct_effect_id");
        this.value = data.getFloat("value");
        this.duration = data.getInt("duration");

        this.setTicking();
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();
        if (entity instanceof ServerPlayerEntity SPE && this.instinctEffectID != null) {
            applySustainedEffect(SPE, this.instinctEffectID, this.value, this.duration);
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("add_sustained_instinct"));
    }

    public static PowerConfiguration<AddSustainedInstinctPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}