package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConditionedManaAttributePower extends PowerType {
    private final @Nullable Identifier modifierID;
    private final @Nullable ManaUtils.Modifier maxManaModifier;
    private final @Nullable ManaUtils.Modifier regenManaModifier;
    private final boolean playerSide;
    private final int tickRate;
    private boolean isAdded = false;

    public static final TypedDataObjectFactory<ConditionedManaAttributePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("modifierID", SerializableDataTypes.IDENTIFIER, null)
                            .add("max_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                            .add("regen_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                            .add("player_side", SerializableDataTypes.BOOLEAN, false)
                            .add("tick_rate", SerializableDataTypes.INT, 20),
                    (data, condition) -> new ConditionedManaAttributePower(
                            condition,
                            data.get("modifierID"),
                            data.get("max_mana_modifier"),
                            data.get("regen_mana_modifier"),
                            data.get("player_side"),
                            data.get("tick_rate")
                    ),
                    (power, sd) -> sd.instance()
            );

    public ConditionedManaAttributePower(Optional<EntityCondition> condition, @Nullable Identifier modifierID,
                                         @Nullable ManaUtils.Modifier maxManaModifier,
                                         @Nullable ManaUtils.Modifier manaRegenModifier,
                                         boolean playerSide, int tickRate) {
        super(condition);
        this.modifierID = modifierID;
        this.maxManaModifier = maxManaModifier;
        this.regenManaModifier = manaRegenModifier;
        this.playerSide = playerSide;
        this.setTicking();
        this.tickRate = tickRate;
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();
        if (entity.age % this.tickRate == 0) {
            if (this.isActive()) {
                if (!isAdded) {
                    this.AddAttr();
                }
            } else {
                if (isAdded) {
                    this.DelAttr();
                }
            }
        }
    }

    public void AddAttr() {
        LivingEntity entity = getHolder();
        this.isAdded = true;
        if (modifierID == null) {
            return;
        }
        if (entity instanceof ServerPlayerEntity playerEntity) {
            if (maxManaModifier != null) {
                ManaUtils.addMaxManaModifier(playerEntity, modifierID, maxManaModifier, playerSide);
            }
            if (regenManaModifier != null) {
                ManaUtils.addRegenManaModifier(playerEntity, modifierID, regenManaModifier, playerSide);
            }
        }
    }

    public void DelAttr() {
        LivingEntity entity = getHolder();
        this.isAdded = false;
        if (modifierID == null) {
            return;
        }
        if (entity instanceof ServerPlayerEntity playerEntity) {
            if (maxManaModifier != null) {
                ManaUtils.removeMaxManaModifier(playerEntity, modifierID, playerSide);
            }
            if (regenManaModifier != null) {
                ManaUtils.removeRegenManaModifier(playerEntity, modifierID, playerSide);
            }
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("conditioned_mana_attribute"));
    }

    public static PowerConfiguration<ConditionedManaAttributePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}