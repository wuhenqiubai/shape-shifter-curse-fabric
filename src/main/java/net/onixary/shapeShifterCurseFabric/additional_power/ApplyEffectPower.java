package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplyEffectPower extends PowerType {

    private final List<StatusEffectInstance> effects;
    private final List<StatusEffectInstance> storeEffects;
    private boolean isApplied = false;

    public static final TypedDataObjectFactory<ApplyEffectPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("status_effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null),
                    (data, condition) -> new ApplyEffectPower(data.get("status_effects"), condition),
                    (power, sd) -> sd.instance().set("status_effects", power.effects)
            );

    public ApplyEffectPower(List<StatusEffectInstance> effects, Optional<EntityCondition> condition) {
        super(condition);
        if (effects == null) {
            effects = new ArrayList<>();
        }
        this.effects = effects;
        this.storeEffects = new ArrayList<>();
    }

    @Override
    public void onGained() {
        this.setTicking();
    }

    @Override
    public void tick() {
        if (this.isActive() && !this.isApplied) {
            this.ApplyEffects();
            this.isApplied = true;
        } else if (!this.isActive() && this.isApplied) {
            this.RemoveEffects();
            this.isApplied = false;
        }
    }

    private void ApplyEffects() {
        LivingEntity entity = getHolder();
        for (StatusEffectInstance effect : this.effects) {
            if (entity.hasStatusEffect(effect.getEffectType())) {
                this.storeEffects.add(entity.getStatusEffect(effect.getEffectType()));
                entity.removeStatusEffect(effect.getEffectType());
            }
            entity.addStatusEffect(new StatusEffectInstance(effect));
        }
    }

    private void RemoveEffects() {
        LivingEntity entity = getHolder();
        for (StatusEffectInstance effect : this.effects) {
            entity.removeStatusEffect(effect.getEffectType());
        }
        for (StatusEffectInstance effect : this.storeEffects) {
            entity.addStatusEffect(effect);
        }
        this.storeEffects.clear();
    }

    @Override
    public void onRemoved() {
        if (this.isApplied) {
            this.RemoveEffects();
        }
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("apply_effect"));
    }

    public static PowerConfiguration<ApplyEffectPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}