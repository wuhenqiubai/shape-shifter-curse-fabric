package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class ChargePower extends PowerType {
    public static class ChargeTier {
        public int tier;
        public boolean enable;
        public int chargeTime;
        public Predicate<Entity> condition;
        public Predicate<Entity> canChargeCondition;
        public Predicate<Entity> autoFireCondition;
        public EntityAction useAction;
        public EntityAction tickAction;
        public EntityAction chargeTickAction;
        public EntityAction chargeCompleteAction;
        public EntityAction chargeCompleteUseAction;
        public EntityAction chargeCompleteTickAction;
        public int cooldown;

        public ChargeTier(SerializableData.Instance data, int index) {
            this.tier = index;
            this.enable = data.getBoolean(String.format("tier%d_enable", index));
            this.chargeTime = data.getInt(String.format("tier%d_charge_time", index));
            this.condition = data.get(String.format("tier%d_condition", index));
            this.canChargeCondition = data.get(String.format("tier%d_can_charge_condition", index));
            this.autoFireCondition = data.get(String.format("tier%d_auto_fire_condition", index));
            this.useAction = data.get(String.format("tier%d_use_action", index));
            this.tickAction = data.get(String.format("tier%d_tick_action", index));
            this.chargeTickAction = data.get(String.format("tier%d_charge_tick_action", index));
            this.chargeCompleteAction = data.get(String.format("tier%d_charge_complete_action", index));
            this.chargeCompleteUseAction = data.get(String.format("tier%d_charge_complete_use_action", index));
            this.chargeCompleteTickAction = data.get(String.format("tier%d_charge_complete_tick_action", index));
            this.cooldown = data.getInt(String.format("tier%d_cooldown", index));
        }

        public void tick(ChargePower power) {
            LivingEntity entity = power.getHolder();
            boolean checkAutoFire = false;
            if (!power.isCharging) {
                return;
            }
            if (power.nowTier + 1 == this.tier) {
                if (this.canChargeCondition != null && !this.canChargeCondition.test(entity)) {
                    power.ChargeTime = power.ChargeTime - 1;
                } else {
                    if (this.chargeTickAction != null) {
                        this.chargeTickAction.accept(new EntityActionContext(entity, entity.getPos()));
                    }
                    if (power.ChargeTime >= this.chargeTime) {
                        if (this.condition != null && !this.condition.test(entity)) {
                            power.ChargeTime = this.chargeTime - 1;
                        } else {
                            if (this.chargeCompleteAction != null) {
                                this.chargeCompleteAction.accept(new EntityActionContext(entity, entity.getPos()));
                            }
                            power.nowTier = this.tier;
                            power.updateTier();
                            checkAutoFire = true;
                        }
                    }
                }
            }
            if (power.nowTier == this.tier) {
                if (this.tickAction != null) {
                    this.tickAction.accept(new EntityActionContext(entity, entity.getPos()));
                }
            }
            if (power.nowTier >= this.tier) {
                if (this.chargeCompleteTickAction != null) {
                    this.chargeCompleteTickAction.accept(new EntityActionContext(entity, entity.getPos()));
                }
            }
            if (checkAutoFire && this.autoFireCondition != null && this.autoFireCondition.test(entity)) {
                power.fire(false);
            }
        }

        public void use(ChargePower power) {
            LivingEntity entity = power.getHolder();
            if (power.nowTier == this.tier) {
                if (this.useAction != null) {
                    this.useAction.accept(new EntityActionContext(entity, entity.getPos()));
                }
                power.nowCooldown = this.cooldown;
            }
            if (power.nowTier >= this.tier) {
                if (this.chargeCompleteUseAction != null) {
                    this.chargeCompleteUseAction.accept(new EntityActionContext(entity, entity.getPos()));
                }
            }
        }
    }

    public static final int TierCount = 10;

    public @Nullable Identifier chargePowerID = null;
    public int nowTier = 0;
    public int renderTier = 0;
    public int ChargeTime = 0;
    public ArrayList<ChargeTier> ChargeTierList = new ArrayList<>();
    public int nowCooldown = 0;

    private boolean isCharging = false;
    private long nowTick = 0;
    private long lastTick = 0;

    public static final TypedDataObjectFactory<ChargePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    buildSerializableData(),
                    (data, condition) -> new ChargePower(condition, data),
                    (power, sd) -> sd.instance()
            );

    private static SerializableData buildSerializableData() {
        SerializableData factoryJson = new SerializableData()
                .add("charge_power_id", SerializableDataTypes.IDENTIFIER, null);
        for (int index = 0; index < TierCount; index++) {
            factoryJson
                    .add(String.format("tier%d_enable", index), SerializableDataTypes.BOOLEAN, index == 0)
                    .add(String.format("tier%d_charge_time", index), SerializableDataTypes.INT, index == 0 ? 0 : -1)
                    .add(String.format("tier%d_condition", index), EntityCondition.DATA_TYPE.optional(), Optional.empty())
                    .add(String.format("tier%d_can_charge_condition", index), EntityCondition.DATA_TYPE.optional(), Optional.empty())
                    .add(String.format("tier%d_auto_fire_condition", index), EntityCondition.DATA_TYPE.optional(), Optional.empty())
                    .add(String.format("tier%d_use_action", index), EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add(String.format("tier%d_tick_action", index), EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add(String.format("tier%d_charge_tick_action", index), EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add(String.format("tier%d_charge_complete_action", index), EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add(String.format("tier%d_charge_complete_use_action", index), EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add(String.format("tier%d_charge_complete_tick_action", index), EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add(String.format("tier%d_cooldown", index), SerializableDataTypes.INT, 0);
        }
        return factoryJson;
    }

    public ChargePower(Optional<EntityCondition> condition, SerializableData.Instance data) {
        super(condition);
        for (int index = 0; index < TierCount; index++) {
            ChargeTier chargeTier = new ChargeTier(data, index);
            if (chargeTier.enable) {
                ChargeTierList.add(chargeTier);
            } else {
                break;
            }
        }
        this.chargePowerID = data.get("charge_power_id");
        this.setTicking();
    }

    public void fire(boolean AddTick) {
        this.isCharging = false;
        if (this.ChargeTime > 0) {
            for (ChargeTier chargeTier : ChargeTierList) {
                chargeTier.use(this);
            }
        }
        if (AddTick) {
            this.nowTick += 2;
        }
        this.nowTier = 0;
        this.ChargeTime = 0;
        this.updateTier();
    }

    @Override
    public void serverTick() {
        if (nowCooldown > 0) {
            nowCooldown--;
        } else {
            nowCooldown = 0;
        }
        if (this.nowTick - this.lastTick > 2) {
            this.fire(false);
        }
        nowTick++;
        for (ChargeTier chargeTier : ChargeTierList) {
            chargeTier.tick(this);
        }
    }

    @Override
    public void onUse() {
        if (nowCooldown > 0) {
            return;
        }
        this.lastTick = nowTick;
        this.isCharging = true;
        this.ChargeTime++;
    }

    public void updateTier() {
        this.renderTier = this.nowTier;
        PowerHolderComponent.sync(getHolder());
    }

    public NbtElement toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("renderTier", this.renderTier);
        return tag;
    }

    public void fromTag(NbtElement tag) {
        this.renderTier = ((NbtCompound) tag).getInt("renderTier");
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("charge_action"));
    }

    public static PowerConfiguration<ChargePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}