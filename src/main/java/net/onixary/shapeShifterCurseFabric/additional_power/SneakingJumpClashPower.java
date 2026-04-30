package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.BiEntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SneakingJumpClashPower extends PowerType {

    private final BiEntityAction bientityAction;
    private final int checkDuration;
    private final double expansionDistance;
    private final float damage;

    private boolean isActive = false;
    private int activeTicks = 0;
    private boolean wasOnGround = true;

    public static final TypedDataObjectFactory<SneakingJumpClashPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("bientity_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("check_duration", SerializableDataTypes.INT, 20)
                            .add("expansion_distance", SerializableDataTypes.DOUBLE, 1.0)
                            .add("damage", SerializableDataTypes.FLOAT, 1.0f),
                    (data, cond) -> new SneakingJumpClashPower(
                            data.get("bientity_action"),
                            data.getInt("check_duration"),
                            data.getDouble("expansion_distance"),
                            data.getFloat("damage"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public SneakingJumpClashPower(Optional<BiEntityAction> bientityAction, int checkDuration,
                                  double expansionDistance, float damage, Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.bientityAction = bientityAction.orElse(null);
        this.checkDuration = checkDuration;
        this.expansionDistance = expansionDistance;
        this.damage = damage;
        this.setTicking();
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();
        if (!(entity instanceof PlayerEntity player) || entity.getWorld().isClient()) {
            return;
        }

        if (player.isOnGround()) {
            wasOnGround = true;
            if (isActive) {
                isActive = false;
                activeTicks = 0;
            }
        } else if (wasOnGround && player.isSneaking() && player.getVelocity().y > 0) {
            isActive = true;
            activeTicks = 0;
            wasOnGround = false;
        }

        if (isActive) {
            activeTicks++;

            if (activeTicks > checkDuration) {
                isActive = false;
                activeTicks = 0;
                return;
            }

            if (checkForCollision(player)) {
                isActive = false;
                activeTicks = 0;
            }
        }
    }

    private boolean checkForCollision(PlayerEntity player) {
        Direction facing = player.getHorizontalFacing();
        Vec3d facingVec = Vec3d.of(facing.getVector());

        Box expandedBox = player.getBoundingBox().stretch(facingVec.multiply(expansionDistance)).expand(0.5);

        for (LivingEntity target : player.getWorld().getEntitiesByClass(
                LivingEntity.class, expandedBox,
                e -> e != player && e.isAlive() && !e.isRemoved())) {

            if (bientityAction != null) {
                this.bientityAction.accept(player, target);
            }

            target.damage(player.getDamageSources().playerAttack(player), damage);
            return true;
        }

        return false;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("sneaking_jump_clash"));
    }

    public static PowerConfiguration<SneakingJumpClashPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}