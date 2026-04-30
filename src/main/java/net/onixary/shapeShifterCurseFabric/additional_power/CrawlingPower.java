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

public class CrawlingPower extends PowerType {
    private float originalEyeHeightScale = 0.0f;
    private float originalHitboxHeightScale = 0.0f;
    private float CRAWL_EYE_HEIGHT_SCALE = 0.0f;
    private float CRAWL_HITBOX_HEIGHT_SCALE = 0.0f;

    public static final TypedDataObjectFactory<CrawlingPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("scale", SerializableDataTypes.FLOAT, 1.0f)
                            .add("eye_scale", SerializableDataTypes.FLOAT, 1.0f)
                            .add("active_scale", SerializableDataTypes.FLOAT, 0.6f)
                            .add("active_eye_scale", SerializableDataTypes.FLOAT, 0.35f),
                    (data, condition) -> new CrawlingPower(
                            condition,
                            data.getFloat("scale"),
                            data.getFloat("eye_scale"),
                            data.getFloat("active_scale"),
                            data.getFloat("active_eye_scale")
                    ),
                    (power, sd) -> sd.instance()
            );

    public CrawlingPower(Optional<EntityCondition> condition, float scale, float eyeScale, float activeScale, float activeEyeScale) {
        super(condition);
        this.setTicking();
        originalHitboxHeightScale = scale;
        originalEyeHeightScale = eyeScale;
        CRAWL_EYE_HEIGHT_SCALE = activeEyeScale;
        CRAWL_HITBOX_HEIGHT_SCALE = activeScale;
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();
        if (entity instanceof ServerPlayerEntity player) {
            if (this.isActive()) {
                ScaleData scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(player);
                ScaleData scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
                scaleDataEyeHeight.setScale(CRAWL_EYE_HEIGHT_SCALE);
                scaleDataHitboxHeight.setScale(CRAWL_HITBOX_HEIGHT_SCALE);
            } else {
                ScaleData scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(player);
                ScaleData scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
                scaleDataEyeHeight.setScale(originalEyeHeightScale);
                scaleDataHitboxHeight.setScale(originalHitboxHeightScale);
            }
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("crawling"));
    }

    public static PowerConfiguration<CrawlingPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}