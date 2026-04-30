package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Optional;

public class ConditionScalePower extends PowerType {
    private ScaleData scaleDataWidth;
    private ScaleData scaleDataHeight;
    private ScaleData scaleDataEyeHeight;
    private ScaleData scaleDataHitboxHeight;

    private final float original_scale;
    private final float original_eye_scale;
    private final float scale;
    private final float eye_scale;

    private boolean IsPowerActive = false;

    public static final TypedDataObjectFactory<ConditionScalePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("scale", SerializableDataTypes.FLOAT, 1.0f)
                            .add("eye_scale", SerializableDataTypes.FLOAT, 1.0f)
                            .add("original_scale", SerializableDataTypes.FLOAT, 1.0f)
                            .add("original_eye_scale", SerializableDataTypes.FLOAT, 1.0f),
                    (data, condition) -> new ConditionScalePower(
                            data.getFloat("scale"),
                            data.getFloat("eye_scale"),
                            data.getFloat("original_scale"),
                            data.getFloat("original_eye_scale"),
                            condition),
                    (power, sd) -> sd.instance()
                            .set("scale", power.scale)
                            .set("eye_scale", power.eye_scale)
                            .set("original_scale", power.original_scale)
                            .set("original_eye_scale", power.original_eye_scale)
            );

    public ConditionScalePower(float scale, float eye_scale, float original_scale, float original_eye_scale, Optional<EntityCondition> condition) {
        super(condition);
        this.scale = scale;
        this.eye_scale = eye_scale;
        this.original_scale = original_scale;
        this.original_eye_scale = original_eye_scale;
    }

    @Override
    public void onGained() {
        if (getHolder() instanceof ServerPlayerEntity) {
            this.scaleDataWidth = ScaleTypes.WIDTH.getScaleData(getHolder());
            this.scaleDataHeight = ScaleTypes.HEIGHT.getScaleData(getHolder());
            this.scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(getHolder());
            this.scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(getHolder());
            this.setTicking();
        }
    }

    @Override
    public void tick() {
        if (this.isActive() && !this.IsPowerActive)  {
            this.ApplyScale();
        } else if (!this.isActive() && this.IsPowerActive) {
            this.RemoveScale();
        }
    }

    public void ApplyScale() {
        this.scaleDataWidth.setScale(scale);
        this.scaleDataWidth.setPersistence(true);
        this.scaleDataHeight.setScale(scale);
        this.scaleDataHeight.setPersistence(true);
        this.scaleDataEyeHeight.setScale(eye_scale);
        this.scaleDataEyeHeight.setPersistence(true);
        this.scaleDataHitboxHeight.setScale(eye_scale);
        this.scaleDataHitboxHeight.setPersistence(true);
        this.IsPowerActive = true;
    }

    public void RemoveScale() {
        this.scaleDataWidth.setScale(original_scale);
        this.scaleDataWidth.setPersistence(true);
        this.scaleDataHeight.setScale(original_scale);
        this.scaleDataHeight.setPersistence(true);
        this.scaleDataEyeHeight.setScale(original_eye_scale);
        this.scaleDataEyeHeight.setPersistence(true);
        this.scaleDataHitboxHeight.setScale(original_eye_scale);
        this.scaleDataHitboxHeight.setPersistence(true);
        this.IsPowerActive = false;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("condition_scale"));
    }

    public static PowerConfiguration<ConditionScalePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}