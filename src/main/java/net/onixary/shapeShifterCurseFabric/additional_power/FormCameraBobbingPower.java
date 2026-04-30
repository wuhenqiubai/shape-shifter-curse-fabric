package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 根据当前形态改变玩家视角晃动方式的客户端 Power。
 * 实际晃动逻辑由 CameraBobbingMixin 负责处理。
 *
 * 数据驱动配置示例:
 * {
 *   "type": "shape-shifter-curse:form_camera_bobbing",
 *   "bobbing_type": "snake"
 * }
 */
public class FormCameraBobbingPower extends PowerType {

    public final String bobbingType;

    public static final TypedDataObjectFactory<FormCameraBobbingPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("bobbing_type", SerializableDataTypes.STRING),
                    (data, condition) -> new FormCameraBobbingPower(data.getString("bobbing_type"), condition),
                    (power, sd) -> sd.instance().set("bobbing_type", power.bobbingType)
            );

    public FormCameraBobbingPower(String bobbingType, Optional<EntityCondition> condition) {
        super(condition);
        this.bobbingType = bobbingType;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("form_camera_bobbing"));
    }

    public static PowerConfiguration<FormCameraBobbingPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}