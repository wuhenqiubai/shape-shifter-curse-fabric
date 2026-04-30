package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class KeepSneakingPower extends PowerType {

    private final EntityCondition condition;

    public static final TypedDataObjectFactory<KeepSneakingPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("condition", EntityCondition.DATA_TYPE.optional(), Optional.empty()),
                    (data, cond) -> new KeepSneakingPower(
                            data.get("condition"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public KeepSneakingPower(Optional<EntityCondition> condition, Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.condition = condition.orElse(null);
    }

    public boolean shouldForceSneak(PlayerEntity player) {
        if (player.isSubmergedInWater() || player.isInsideWaterOrBubbleColumn()) {
            return false;
        }
        return (condition == null || condition.test(getHolder()));
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("keep_sneaking"));
    }

    public static PowerConfiguration<KeepSneakingPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}