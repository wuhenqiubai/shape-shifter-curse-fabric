package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DelayAttributePower extends PowerType {
    private final int TargetDelay;
    private int NowDelay;
    private final List<AttributedEntityAttributeModifier> modifiers = new LinkedList<>();
    private final boolean updateHealth;
    private final int tickRate;
    private boolean IsModActive = false;

    public static final TypedDataObjectFactory<DelayAttributePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("tick_rate", SerializableDataTypes.INT, 1)
                            .add("updateHealth", SerializableDataTypes.BOOLEAN, true)
                            .add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
                            .add("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, null)
                            .add("delay", SerializableDataTypes.INT, 0),
                    (data, condition) -> new DelayAttributePower(condition, data),
                    (power, sd) -> sd.instance()
            );

    public DelayAttributePower(Optional<EntityCondition> condition, SerializableData.Instance data) {
        super(condition);
        this.tickRate = data.getInt("tick_rate");
        this.updateHealth = data.getBoolean("updateHealth");
        this.TargetDelay = data.getInt("delay");
        this.NowDelay = this.TargetDelay;
        if (data.isPresent("modifier")) {
            this.addModifier((AttributedEntityAttributeModifier) data.get("modifier"));
        }
        if (data.isPresent("modifiers")) {
            List<AttributedEntityAttributeModifier> modifierList = (List) data.get("modifiers");
            modifierList.forEach(this::addModifier);
        }
        this.setTicking();
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();
        if (entity.age % this.tickRate == 0) {
            if (this.isActive()) {
                if (!this.IsModActive) {
                    if (this.NowDelay >= this.TargetDelay) {
                        this.addMods();
                        this.NowDelay = 0;
                        return;
                    }
                    this.NowDelay++;
                    return;
                } else {
                    this.addMods();
                }
            } else {
                if (this.IsModActive) {
                    if (this.NowDelay >= this.TargetDelay) {
                        this.removeMods();
                        this.NowDelay = 0;
                        return;
                    }
                    this.NowDelay++;
                    return;
                } else {
                    this.removeMods();
                }
            }
            this.NowDelay = 0;
        }
    }

    @Override
    public void onRemoved() {
        this.removeMods();
    }

    public void addMods() {
        LivingEntity entity = getHolder();
        this.IsModActive = true;
        float previousMaxHealth = entity.getMaxHealth();
        float previousHealthPercent = entity.getHealth() / previousMaxHealth;
        this.modifiers.forEach((mod) -> {
            if (entity.getAttributes().hasAttribute(net.minecraft.registry.entry.RegistryEntry.of(mod.getAttribute()))) {
                EntityAttributeInstance instance = entity.getAttributeInstance(net.minecraft.registry.entry.RegistryEntry.of(mod.getAttribute()));
                if (instance != null && !instance.hasModifier(mod.getModifier())) {
                    instance.addTemporaryModifier(mod.getModifier());
                }
            }
        });
        float afterMaxHealth = entity.getMaxHealth();
        if (this.updateHealth && afterMaxHealth != previousMaxHealth) {
            entity.setHealth(afterMaxHealth * previousHealthPercent);
        }
    }

    public void removeMods() {
        LivingEntity entity = getHolder();
        this.IsModActive = false;
        float previousMaxHealth = entity.getMaxHealth();
        float previousHealthPercent = entity.getHealth() / previousMaxHealth;
        this.modifiers.forEach((mod) -> {
            if (entity.getAttributes().hasAttribute(net.minecraft.registry.entry.RegistryEntry.of(mod.getAttribute()))) {
                EntityAttributeInstance instance = entity.getAttributeInstance(net.minecraft.registry.entry.RegistryEntry.of(mod.getAttribute()));
                if (instance != null && instance.hasModifier(mod.getModifier())) {
                    instance.removeModifier(mod.getModifier());
                }
            }
        });
        float afterMaxHealth = entity.getMaxHealth();
        if (this.updateHealth && afterMaxHealth != previousMaxHealth) {
            entity.setHealth(afterMaxHealth * previousHealthPercent);
        }
    }

    public DelayAttributePower addModifier(EntityAttribute attribute, EntityAttributeModifier modifier) {
        AttributedEntityAttributeModifier mod = new AttributedEntityAttributeModifier(attribute, modifier);
        this.modifiers.add(mod);
        return this;
    }

    public DelayAttributePower addModifier(AttributedEntityAttributeModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("delay_attribute"));
    }

    public static PowerConfiguration<DelayAttributePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}