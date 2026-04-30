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

public class ScalePower extends PowerType {

    private final float scale;
    private final float eyeScale;

    public static final TypedDataObjectFactory<ScalePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("scale", SerializableDataTypes.FLOAT, 1.0f)
                            .add("eye_scale", SerializableDataTypes.FLOAT, 1.0f),
                    (data, condition) -> new ScalePower(data.getFloat("scale"), data.getFloat("eye_scale"), condition),
                    (power, sd) -> sd.instance()
                            .set("scale", power.scale)
                            .set("eye_scale", power.eyeScale)
            );

    public ScalePower(float scale, float eyeScale, Optional<EntityCondition> condition) {
        super(condition);
        this.scale = scale;
        this.eyeScale = eyeScale;
    }

    @Override
    public void onGained() {
        if (getHolder() instanceof ServerPlayerEntity) {
            ScaleData scaleDataWidth = ScaleTypes.WIDTH.getScaleData(getHolder());
            ScaleData scaleDataHeight = ScaleTypes.HEIGHT.getScaleData(getHolder());
            scaleDataWidth.setScale(scale);
            scaleDataWidth.setPersistence(true);
            scaleDataHeight.setScale(scale);
            scaleDataHeight.setPersistence(true);
            ScaleData scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(getHolder());
            ScaleData scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(getHolder());
            scaleDataEyeHeight.setScale(eyeScale);
            scaleDataEyeHeight.setPersistence(true);
            scaleDataHitboxHeight.setScale(eyeScale);
            scaleDataHitboxHeight.setPersistence(true);
        }
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("scale"));
    }

    public static PowerConfiguration<ScalePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}