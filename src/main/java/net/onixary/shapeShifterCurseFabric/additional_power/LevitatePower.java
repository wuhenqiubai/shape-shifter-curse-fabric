package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mixin.accessor.ApoliClientAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LevitatePower extends PowerType {
    private float ascentSpeed = 0.5f;
    private int maxAscendDuration = 40;

    private int ascendProgress = 0;
    private boolean wasActiveLastTick = false;
    private boolean isKeyActive = false;
    private boolean isLevitate = false;

    public static final TypedDataObjectFactory<LevitatePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("ascent_speed", SerializableDataTypes.FLOAT, 0.5f)
                            .add("max_ascend_duration", SerializableDataTypes.INT, 40)
                            .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER),
                    (data, condition) -> {
                        LevitatePower power = new LevitatePower(condition);
                        power.ascentSpeed = data.getFloat("ascent_speed");
                        power.maxAscendDuration = data.getInt("max_ascend_duration");
                        return power;
                    },
                    (power, sd) -> sd.instance()
            );

    public LevitatePower(Optional<EntityCondition> condition) {
        super(condition);
        this.setTicking();
    }

    @Override
    public void onUse() {
        LivingEntity entity = getHolder();
        if (entity instanceof PlayerEntity player) {
            this.isKeyActive = true;
            PowerHolderComponent.sync(entity);
        }
    }

    private void resetLevitateState() {
        this.isLevitate = false;
        this.ascendProgress = 0;
    }

    private float easeOutQuadProgress() {
        float a = (1 - ((float) this.ascendProgress / (float) this.maxAscendDuration));
        return a * a;
    }

    private void processLevitate(PlayerEntity player) {
        this.isLevitate = true;
        player.setNoGravity(true);
    }

    private void processStopLevitate(PlayerEntity player) {
        this.isLevitate = false;
        player.setNoGravity(false);
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();
        if (entity instanceof PlayerEntity player) {
            if (player.getFluidHeight(FluidTags.WATER) > 0.0F || player.getFluidHeight(FluidTags.LAVA) > 0.0F) {
                this.resetLevitateState();
                return;
            }

            if (this.isKeyActive || this.wasActiveLastTick) {
                this.processLevitate(player);
            } else {
                this.processStopLevitate(player);
            }

            player.fallDistance = 0;

            this.wasActiveLastTick = this.isKeyActive;
            this.isKeyActive = false;
            if (entity.isOnGround()) {
                this.resetLevitateState();
            }
            PowerHolderComponent.sync(entity);
        }
    }

    @Environment(EnvType.CLIENT)
    private void ClientProcessLevitate(PlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        if (this.ascendProgress < this.maxAscendDuration) {
            player.setVelocity(velocity.x, this.ascentSpeed * easeOutQuadProgress(), velocity.z);
            this.ascendProgress++;
        } else {
            player.setVelocity(velocity.x, 0, velocity.z);
        }
    }

    @Environment(EnvType.CLIENT)
    private void ClientProcessStopLevitate(PlayerEntity player) {
    }

    @Environment(EnvType.CLIENT)
    public boolean clientIsActive() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public void clientTick(PlayerEntity player) {
        LivingEntity entity = getHolder();
        if (player.getFluidHeight(FluidTags.WATER) > 0.0F || player.getFluidHeight(FluidTags.LAVA) > 0.0F) {
            this.resetLevitateState();
            return;
        }
        this.isKeyActive = this.clientIsActive();
        if (this.isKeyActive || this.wasActiveLastTick) {
            this.ClientProcessLevitate(player);
        } else {
            this.ClientProcessStopLevitate(player);
        }
        this.wasActiveLastTick = this.isKeyActive;
        this.isKeyActive = false;
        if (entity.isOnGround()) {
            this.resetLevitateState();
        }
    }

    @Override
    public boolean isActive() {
        return this.isLevitate && super.isActive();
    }

    @Override
    public void onRemoved() {
        LivingEntity entity = getHolder();
        entity.setNoGravity(false);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("levitate"));
    }

    public static PowerConfiguration<LevitatePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}