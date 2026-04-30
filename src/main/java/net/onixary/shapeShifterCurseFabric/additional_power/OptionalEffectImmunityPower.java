package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class OptionalEffectImmunityPower extends PowerType {

    private final List<StatusEffect> effects;
    private final boolean inverted;

    public static final TypedDataObjectFactory<OptionalEffectImmunityPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("effect", SerializableDataTypes.IDENTIFIER, null)
                            .add("effects", SerializableDataTypes.IDENTIFIERS, null)
                            .add("inverted", SerializableDataTypes.BOOLEAN, false),
                    (data, condition) -> {
                        OptionalEffectImmunityPower power = new OptionalEffectImmunityPower(data.getBoolean("inverted"), condition);
                        if (data.isPresent("effect")) {
                            StatusEffect effect = getStatusEffect(data.get("effect"));
                            if (effect != null) {
                                power.addEffect(effect);
                            }
                        }
                        if (data.isPresent("effects")) {
                            List<Identifier> effectIDs = data.get("effects");
                            for (Identifier effectID : effectIDs) {
                                StatusEffect effect = getStatusEffect(effectID);
                                if (effect != null) {
                                    power.addEffect(effect);
                                }
                            }
                        }
                        return power;
                    },
                    (power, sd) -> sd.instance()
                            .set("inverted", power.inverted)
            );

    public OptionalEffectImmunityPower(boolean inverted, Optional<EntityCondition> condition) {
        super(condition);
        this.inverted = inverted;
        this.effects = new java.util.ArrayList<>();
    }

    public void addEffect(StatusEffect effect) {
        this.effects.add(effect);
    }

    public boolean doesApply(StatusEffect effect) {
        if (inverted) {
            return !effects.contains(effect);
        }
        return effects.contains(effect);
    }

    public static @Nullable StatusEffect getStatusEffect(Identifier effectID) {
        Optional<StatusEffect> result = Registries.STATUS_EFFECT.getOrEmpty(effectID);
        return result.orElse(null);
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("optional_effect_immunity"));
    }

    public static PowerConfiguration<OptionalEffectImmunityPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}