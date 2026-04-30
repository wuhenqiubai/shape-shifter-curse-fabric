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
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Optional;

public class ModifyStepHeightPower extends PowerType {

    private final float stepHeightScale;
    private final EntityCondition condition;
    private final boolean affectSneak;

    public static final TypedDataObjectFactory<ModifyStepHeightPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("step_height_scale", SerializableDataTypes.FLOAT)
                            .add("condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                            .add("affect_sneak", SerializableDataTypes.BOOLEAN, true),
                    (data, condition) -> new ModifyStepHeightPower(
                            data.getFloat("step_height_scale"),
                            data.get("condition"),
                            data.getBoolean("affect_sneak"),
                            condition),
                    (power, sd) -> sd.instance()
                            .set("step_height_scale", power.stepHeightScale)
                            .set("condition", power.condition)
                            .set("affect_sneak", power.affectSneak)
            );

    public ModifyStepHeightPower(float stepHeightScale, EntityCondition condition, boolean affectSneak,
                                  Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.stepHeightScale = stepHeightScale;
        this.condition = condition;
        this.affectSneak = affectSneak;
    }

    @Override
    public void onGained() {
        this.setTicking();
    }

    @Override
    public void tick() {
        LivingEntity entity = getHolder();
        if (entity instanceof ServerPlayerEntity) {
            boolean isEffective = (condition == null || condition.test(entity));
            ScaleData scaleDataStepHeight = ScaleTypes.STEP_HEIGHT.getScaleData(entity);

            if (isEffective) {
                scaleDataStepHeight.setScale(stepHeightScale);
                scaleDataStepHeight.setPersistence(true);
            } else {
                scaleDataStepHeight.setScale(1.0f);
                scaleDataStepHeight.setPersistence(true);
            }
        }
    }

    @Override
    public void onRemoved() {
        LivingEntity entity = getHolder();
        if (entity != null) {
            ScaleData scaleDataStepHeight = ScaleTypes.STEP_HEIGHT.getScaleData(entity);
            scaleDataStepHeight.setScale(1.0f);
            scaleDataStepHeight.setPersistence(true);
        }
    }

    @Override
    public void onLost() {
        LivingEntity entity = getHolder();
        if (entity != null) {
            ScaleData scaleDataStepHeight = ScaleTypes.STEP_HEIGHT.getScaleData(entity);
            scaleDataStepHeight.setScale(1.0f);
            scaleDataStepHeight.setPersistence(true);
        }
    }

    public boolean shouldAffectSneak() {
        return affectSneak;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("modify_step_height"));
    }

    public static PowerConfiguration<ModifyStepHeightPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}