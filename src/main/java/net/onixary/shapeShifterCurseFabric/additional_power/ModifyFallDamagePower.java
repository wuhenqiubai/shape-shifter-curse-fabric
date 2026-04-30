package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ModifyFallDamagePower extends PowerType {

    private final List<Modifier> Modifiers_FallDistance = new LinkedList<>();
    private final List<Modifier> Modifiers_DamageMultiplier = new LinkedList<>();

    public static final TypedDataObjectFactory<ModifyFallDamagePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("modifier_fall_distance", Modifier.DATA_TYPE, null)
                            .add("modifiers_fall_distance", Modifier.LIST_TYPE, null)
                            .add("modifier_damage_multiplier", Modifier.DATA_TYPE, null)
                            .add("modifiers_damage_multiplier", Modifier.LIST_TYPE, null),
                    (data, condition) -> {
                        ModifyFallDamagePower power = new ModifyFallDamagePower(condition);
                        data.ifPresent("modifier_fall_distance", power::addModifier_FallDistance);
                        data.<List<Modifier>>ifPresent("modifiers_fall_distance", mods -> mods.forEach(power::addModifier_FallDistance));
                        data.ifPresent("modifier_damage_multiplier", power::addModifier_DamageMultiplier);
                        data.<List<Modifier>>ifPresent("modifiers_damage_multiplier", mods -> mods.forEach(power::addModifier_DamageMultiplier));
                        return power;
                    },
                    (power, sd) -> sd.instance()
            );

    public ModifyFallDamagePower(Optional<EntityCondition> condition) {
        super(condition);
    }

    public void addModifier_FallDistance(Modifier modifier) {
        this.Modifiers_FallDistance.add(modifier);
    }

    public List<Modifier> getModifiers_FallDistance() {
        return Modifiers_FallDistance;
    }

    public void addModifier_DamageMultiplier(Modifier modifier) {
        this.Modifiers_DamageMultiplier.add(modifier);
    }

    public List<Modifier> getModifiers_DamageMultiplier() {
        return Modifiers_DamageMultiplier;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("modfiy_fall_damage"));
    }

    public static PowerConfiguration<ModifyFallDamagePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}