package net.onixary.shapeShifterCurseFabric.additional_power;

import blue.endless.jankson.annotation.Nullable;
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

import java.util.Optional;

public class ManaTypePower extends PowerType {
    private @Nullable Identifier manaType = null;
    private @Nullable Identifier manaSource = null;

    public static final TypedDataObjectFactory<ManaTypePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("mana_type", SerializableDataTypes.IDENTIFIER, null)
                            .add("mana_source", SerializableDataTypes.IDENTIFIER, null),
                    (data, condition) -> new ManaTypePower(condition, data.get("mana_type"), data.get("mana_source")),
                    (power, sd) -> sd.instance()
            );

    public ManaTypePower(Optional<EntityCondition> condition, @Nullable Identifier manaType, @Nullable Identifier manaSource) {
        super(condition);
        this.manaType = manaType;
        if (manaSource == null) {
            this.manaSource = getConfig().id();
        } else {
            this.manaSource = manaSource;
        }
    }

    @Override
    public void onAdded() {
        LivingEntity entity = getHolder();
        if (entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            if (!ManaUtils.isManaTypeExists(playerEntity, manaType, manaSource)) {
                ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
            }
        }
    }

    @Override
    public void onGained() {
        LivingEntity entity = getHolder();
        if (entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            if (!ManaUtils.isManaTypeExists(playerEntity, manaType, manaSource)) {
                ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
            }
            ManaUtils.gainPlayerMana(playerEntity, Double.MAX_VALUE / 8);
        }
    }

    @Override
    public void onLost() {
        LivingEntity entity = getHolder();
        if (entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            if (ManaUtils.isManaTypeExists(playerEntity, manaType, manaSource)) {
                ManaUtils.loseManaTypeID(playerEntity, manaType, manaSource);
            }
        }
    }

    @Override
    public void onRespawn() {
        LivingEntity entity = getHolder();
        if (entity instanceof ServerPlayerEntity playerEntity && manaType != null) {
            if (!ManaUtils.isManaTypeExists(playerEntity, manaType, manaSource)) {
                ManaUtils.gainManaTypeID(playerEntity, manaType, manaSource);
            }
            ManaUtils.gainPlayerMana(playerEntity, Double.MAX_VALUE / 8);
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("mana_type_power"));
    }

    public static PowerConfiguration<ManaTypePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}