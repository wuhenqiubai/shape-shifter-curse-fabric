package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AttractByEntityPower extends PowerType {

    private final EntityCondition entityCondition;
    private final EntityAction entityAction;
    private final EntityAction selfAction;
    private final float attractionSpeed;
    private final float attractionRadius;
    private final float stopRadius;
    private final float escapeAttractionSpeed;
    private final float escapeAngleThreshold;

    private int tickCounter = 0;
    private Entity targetEntity;

    public static final TypedDataObjectFactory<AttractByEntityPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                            .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("self_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("attraction_speed", SerializableDataTypes.FLOAT, 0.1f)
                            .add("attraction_radius", SerializableDataTypes.FLOAT, 8.0f)
                            .add("stop_radius", SerializableDataTypes.FLOAT, 1.0f)
                            .add("escape_attraction_speed", SerializableDataTypes.FLOAT, 0.025f)
                            .add("escape_angle", SerializableDataTypes.FLOAT, (float) Math.toRadians(80)),
                    (data, cond) -> new AttractByEntityPower(
                            data.get("entity_condition"),
                            data.get("entity_action"),
                            data.get("self_action"),
                            data.getFloat("attraction_speed"),
                            data.getFloat("attraction_radius"),
                            data.getFloat("stop_radius"),
                            data.getFloat("escape_attraction_speed"),
                            data.getFloat("escape_angle"),
                            cond),
                    (power, sd) -> sd.instance()
            );

    public AttractByEntityPower(Optional<EntityCondition> entityCondition, Optional<EntityAction> entityAction,
                                Optional<EntityAction> selfAction, float attractionSpeed, float attractionRadius,
                                float stopRadius, float escapeAttractionSpeed, float escapeAngleThreshold,
                                Optional<EntityCondition> powerCondition) {
        super(powerCondition);
        this.entityCondition = entityCondition.orElse(null);
        this.entityAction = entityAction.orElse(null);
        this.selfAction = selfAction.orElse(null);
        this.attractionSpeed = attractionSpeed;
        this.attractionRadius = attractionRadius;
        this.stopRadius = stopRadius;
        this.escapeAttractionSpeed = escapeAttractionSpeed;
        this.escapeAngleThreshold = escapeAngleThreshold;
        this.setTicking();
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();
        if (!(entity instanceof PlayerEntity player) || player.isSpectator()) {
            return;
        }

        if (tickCounter++ % 5 == 0) {
            Box searchBox = Box.from(player.getPos()).expand(attractionRadius);
            List<Entity> entities = player.getWorld().getOtherEntities(
                    player,
                    searchBox,
                    e -> entityCondition == null || entityCondition.test(e)
            );

            targetEntity = entities.stream()
                    .filter(e -> e.isAlive() && !e.isSpectator())
                    .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(player)))
                    .orElse(null);

            if (targetEntity != null && player.squaredDistanceTo(targetEntity) < stopRadius * stopRadius) {
                targetEntity = null;
            }

            if (isPlayerInVehicle(player) || !player.isOnGround()) {
                targetEntity = null;
                return;
            }

            if (targetEntity != null) {
                Vec3d actorEyePos = entity.getEyePos();
                Vec3d targetEyePos = targetEntity.getEyePos();
                RaycastContext context = new RaycastContext(actorEyePos, targetEyePos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);
                if (entity.getWorld().raycast(context).getType() == HitResult.Type.BLOCK) {
                    targetEntity = null;
                }
            }
        }

        if (targetEntity != null) {
            Vec3d attractDirection = targetEntity.getPos()
                    .subtract(player.getPos())
                    .multiply(1, 0, 1)
                    .normalize();

            Vec3d currentVelocity = player.getVelocity();
            Vec3d horizontalVelocity = new Vec3d(currentVelocity.x, 0, currentVelocity.z);
            Vec3d playerFacing = player.getRotationVector().multiply(1, 0, 1).normalize();

            float effectiveSpeed = calculateEffectiveSpeed(attractDirection, horizontalVelocity, playerFacing);

            Vec3d finalVelocity = attractDirection.multiply(effectiveSpeed).add(0, currentVelocity.y, 0);
            player.setVelocity(finalVelocity);
            player.velocityModified = true;

            if (entityAction != null) {
                entityAction.accept(targetEntity);
            }

            if (selfAction != null) {
                selfAction.accept(player);
            }

            PowerHolderComponent.sync(player);
        }
    }

    private boolean isPlayerInVehicle(PlayerEntity player) {
        Entity vehicle = player.getVehicle();
        return vehicle != null && (
                vehicle instanceof BoatEntity ||
                        vehicle instanceof MinecartEntity ||
                        vehicle.getType().getUntranslatedName().contains("vehicle") ||
                        vehicle.getType().getUntranslatedName().contains("mount")
        );
    }

    private float calculateEffectiveSpeed(Vec3d attractDirection, Vec3d playerVelocity, Vec3d playerFacing) {
        Vec3d faceDirection = playerFacing.normalize();
        double dotProduct = faceDirection.dotProduct(attractDirection);
        if (dotProduct < 0) {
            return escapeAttractionSpeed;
        }
        return attractionSpeed;
    }

    @Override
    public void onGained() {
        targetEntity = null;
    }

    @Override
    public void onLost() {
        targetEntity = null;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("attract_by_entity"));
    }

    public static PowerConfiguration<AttractByEntityPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}