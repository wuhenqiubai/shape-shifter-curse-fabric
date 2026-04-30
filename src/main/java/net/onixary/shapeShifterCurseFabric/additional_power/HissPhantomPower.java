package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.BiEntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HissPhantomPower extends PowerType {

    private final @Nullable BiEntityAction onHissPhantomAction;

    public static final TypedDataObjectFactory<HissPhantomPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("on_hiss_phantom_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty()),
                    (data, cond) -> new HissPhantomPower(
                            data.get("on_hiss_phantom_action"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public HissPhantomPower(Optional<BiEntityAction> onHissPhantomAction, Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.onHissPhantomAction = onHissPhantomAction.orElse(null);
    }

    public void invokeAction(LivingEntity powerOwner, PhantomEntity phantom) {
        if (this.onHissPhantomAction != null) {
            this.onHissPhantomAction.accept(powerOwner, phantom);
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("hiss_phantom_power"));
    }

    public static PowerConfiguration<HissPhantomPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}