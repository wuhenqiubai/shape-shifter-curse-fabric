package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ManaAttributePower extends PowerType {

    private final @Nullable Identifier modifierID;
    private final @Nullable ManaUtils.Modifier maxManaModifier;
    private final @Nullable ManaUtils.Modifier regenManaModifier;
    private final boolean playerSide;

    public static final TypedDataObjectFactory<ManaAttributePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("modifierID", SerializableDataTypes.IDENTIFIER, null)
                            .add("max_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                            .add("regen_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                            .add("player_side", SerializableDataTypes.BOOLEAN, false),
                    (data, cond) -> new ManaAttributePower(
                            data.get("modifierID"),
                            data.get("max_mana_modifier"),
                            data.get("regen_mana_modifier"),
                            data.get("player_side"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public ManaAttributePower(@Nullable Identifier modifierID, @Nullable ManaUtils.Modifier maxManaModifier,
                              @Nullable ManaUtils.Modifier regenManaModifier, boolean playerSide,
                              Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.modifierID = modifierID;
        this.maxManaModifier = maxManaModifier;
        this.regenManaModifier = regenManaModifier;
        this.playerSide = playerSide;
    }

    @Override
    public void onGained() {
        if (modifierID == null) {
            return;
        }
        if (getHolder() instanceof ServerPlayerEntity playerEntity) {
            if (maxManaModifier != null) {
                ManaUtils.addMaxManaModifier(playerEntity, modifierID, maxManaModifier, playerSide);
            }
            if (regenManaModifier != null) {
                ManaUtils.addRegenManaModifier(playerEntity, modifierID, regenManaModifier, playerSide);
            }
        }
    }

    @Override
    public void onLost() {
        if (modifierID == null) {
            return;
        }
        if (getHolder() instanceof ServerPlayerEntity playerEntity) {
            if (maxManaModifier != null) {
                ManaUtils.removeMaxManaModifier(playerEntity, modifierID, playerSide);
            }
            if (regenManaModifier != null) {
                ManaUtils.removeRegenManaModifier(playerEntity, modifierID, playerSide);
            }
        }
    }

    @Override
    public void onRespawn() {
        this.onGained();
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("mana_attribute"));
    }

    public static PowerConfiguration<ManaAttributePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}