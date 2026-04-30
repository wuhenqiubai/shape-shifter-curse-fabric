package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ProjectileDodgePower extends PowerType {
    private final EntityAction action;
    private final Predicate<LivingEntity> entityCondition;
    private final double range;
    private final double dodgeSpeed;
    private final double triggerDistance;
    private final int cooldown;
    private int timer;
    private boolean dodgeRight;

    public static final TypedDataObjectFactory<ProjectileDodgePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                            .add("range", SerializableDataTypes.DOUBLE, 5.0)
                            .add("dodge_speed", SerializableDataTypes.DOUBLE, 1.0)
                            .add("trigger_distance", SerializableDataTypes.DOUBLE, 4.0)
                            .add("cooldown", SerializableDataTypes.INT, 20),
                    (data, condition) -> new ProjectileDodgePower(
                            data.get("action"),
                            data.get("entity_condition"),
                            data.getDouble("range"),
                            data.getDouble("dodge_speed"),
                            data.getDouble("trigger_distance"),
                            data.getInt("cooldown"),
                            condition),
                    (power, sd) -> sd.instance()
                            .set("action", power.action)
                            .set("entity_condition", power.entityCondition)
                            .set("range", power.range)
                            .set("dodge_speed", power.dodgeSpeed)
                            .set("trigger_distance", power.triggerDistance)
                            .set("cooldown", power.cooldown)
            );

    public ProjectileDodgePower(EntityAction action, Predicate<LivingEntity> entityCondition,
                                 double range, double dodgeSpeed, double triggerDistance, int cooldown,
                                 Optional<EntityCondition> condition) {
        super(condition);
        this.action = action;
        this.entityCondition = entityCondition;
        this.range = range;
        this.dodgeSpeed = dodgeSpeed;
        this.triggerDistance = triggerDistance;
        this.cooldown = cooldown;
        this.timer = 0;
        this.dodgeRight = true;
    }

    @Override
    public void onGained() {
        this.setTicking();
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();
        if (!(entity instanceof PlayerEntity player) || player.isSpectator()) return;
        if (player.getWorld().isClient()) return;

        if (entityCondition != null && !entityCondition.test(player)) { return; }

        List<ProjectileEntity> projectiles = player.getWorld()
                .getEntitiesByClass(ProjectileEntity.class,
                        player.getBoundingBox().expand(range),
                        projectile -> {
                            if (projectile.getOwner() == player) {
                                return false;
                            }
                            if (projectile.isOnGround()) {
                                return false;
                            }
                            Vec3d prevPos = projectile.prevX != 0 ? new Vec3d(projectile.prevX, projectile.prevY, projectile.prevZ) : null;
                            if (prevPos != null) {
                                double movement = prevPos.distanceTo(projectile.getPos());
                                if (movement < 0.1) {
                                    return false;
                                }
                            }
                            return true;
                        });

        if (timer > 0) {
            timer--;
        }

        for (ProjectileEntity projectile : projectiles) {
            if (isProjectileApproaching(projectile, player)) {
                if (timer <= 0) {
                    performDodge(player, projectile);
                    timer = cooldown;
                }
                break;
            }
        }
    }

    private boolean isProjectileApproaching(ProjectileEntity projectile, PlayerEntity player) {
        double distance = projectile.getPos().distanceTo(player.getPos());
        if (distance > triggerDistance) return false;

        Vec3d toPlayer = player.getPos().subtract(projectile.getPos()).normalize();
        Vec3d projectileVelocity = projectile.getVelocity().normalize();
        double dot = toPlayer.dotProduct(projectileVelocity);
        return dot > 0.7;
    }

    private void performDodge(PlayerEntity player, ProjectileEntity projectile) {
        Vec3d projectileVel = projectile.getVelocity();
        Vec3d horizontalVel = new Vec3d(projectileVel.x, 0, projectileVel.z).normalize();

        dodgeRight = !dodgeRight;

        Vec3d dodgeDirection;
        if (dodgeRight) {
            dodgeDirection = new Vec3d(-horizontalVel.z, 0, horizontalVel.x).normalize();
        } else {
            dodgeDirection = new Vec3d(horizontalVel.z, 0, -horizontalVel.x).normalize();
        }
        ShapeShifterCurseFabric.LOGGER.info("正在躲避投射物: " + projectile.getUuid());
        player.addVelocity(dodgeDirection.multiply(dodgeSpeed));
        player.velocityModified = true;
        if (action != null) {
            action.accept(player);
        }
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("projectile_dodge"));
    }

    public static PowerConfiguration<ProjectileDodgePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}