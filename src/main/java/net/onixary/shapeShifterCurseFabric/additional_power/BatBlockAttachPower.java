package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.condition.BlockCondition;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimRegistries;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

import static net.onixary.shapeShifterCurseFabric.additional_power.BatAttachEventHandler.getBatAttachPower;

public class BatBlockAttachPower extends PowerType {

    private final Predicate<Entity> entityAttachCondition;
    private final Predicate<CachedBlockPosition> blockCondition;
    private final EntityAction sideAttachAction;
    private final EntityAction bottomAttachAction;
    private final int bottomAttachInterval;

    private boolean isAttached = false;
    private AttachType attachType = AttachType.NONE;
    private BlockPos attachedBlockPos = null;
    private Direction attachedSide = null;
    private int bottomAttachTimer = 0;

    public enum AttachType {
        NONE, SIDE, BOTTOM
    }

    public static final TypedDataObjectFactory<BatBlockAttachPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("attach_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                            .add("block_condition", BlockCondition.DATA_TYPE, null)
                            .add("side_attach_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("bottom_attach_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("bottom_attach_interval", SerializableDataTypes.INT, 20),
                    (data, condition) -> new BatBlockAttachPower(
                            condition,
                            data.get("attach_condition"),
                            data.get("block_condition"),
                            data.get("side_attach_action"),
                            data.get("bottom_attach_action"),
                            data.getInt("bottom_attach_interval")
                    ),
                    (power, sd) -> sd.instance()
            );

    public BatBlockAttachPower(Optional<EntityCondition> condition,
                               Predicate<Entity> entityAttachCondition,
                               Predicate<CachedBlockPosition> blockCondition,
                               EntityAction sideAttachAction,
                               EntityAction bottomAttachAction,
                               int bottomAttachInterval) {
        super(condition);
        this.entityAttachCondition = entityAttachCondition;
        this.blockCondition = blockCondition;
        this.sideAttachAction = sideAttachAction;
        this.bottomAttachAction = bottomAttachAction;
        this.bottomAttachInterval = bottomAttachInterval;
        this.setTicking();
        this.shouldTickWhenInactive();
    }

    public static void syncClientState(PlayerEntity player, boolean isAttached, int attachTypeOrdinal,
                                       BlockPos attachedPos, Direction attachedSide) {
        BatBlockAttachPower power = getBatAttachPower(player);
        if (power != null) {
            power.isAttached = isAttached;
            power.attachType = AttachType.values()[attachTypeOrdinal];
            power.attachedBlockPos = attachedPos;
            power.attachedSide = attachedSide;
            power.bottomAttachTimer = 0;

            if (isAttached && attachedPos != null && attachedSide != null) {
                Vec3d targetPos;
                if (power.attachType == AttachType.SIDE) {
                    targetPos = Vec3d.ofCenter(attachedPos).add(Vec3d.of(attachedSide.getVector()).multiply(0.75d)).add(0, -0.5, 0);
                } else {
                    targetPos = Vec3d.ofCenter(attachedPos).add(0, -1.5, 0);
                }

                player.setPosition(targetPos.x, targetPos.y, targetPos.z);
                player.setVelocity(Vec3d.ZERO);
            }
        }
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        if (!player.getWorld().isClient()) {
            if (isAttached && attachedBlockPos != null) {
                World world = entity.getWorld();
                BlockState blockState = world.getBlockState(attachedBlockPos);

                if (blockState.isAir()) {
                    detach(player, false);
                    return;
                }
            }

            if (isAttached && attachType == AttachType.BOTTOM && bottomAttachAction != null) {
                bottomAttachTimer++;
                if (bottomAttachTimer >= bottomAttachInterval) {
                    bottomAttachAction.accept(new EntityActionContext(entity, entity.getPos()));
                    bottomAttachTimer = 0;
                }
            }
        }

        if (isAttached) {
            maintainAttachPosition(player);

            if (attachType == AttachType.SIDE && attachedSide != null) {
                maintainFacingDirection(player);
            }
        }
    }

    public boolean tryAttach(PlayerEntity player, BlockHitResult hitResult) {
        if (isAttached || player.isOnGround()) {
            return false;
        }

        if (this.entityAttachCondition != null && !this.entityAttachCondition.test(player)) {
            return false;
        }

        BlockPos blockPos = hitResult.getBlockPos();

        if (blockCondition != null && !blockCondition.test(new CachedBlockPosition(getHolder().getWorld(), blockPos, true))) {
            return false;
        }

        Direction hitSide = hitResult.getSide();

        boolean attached = false;
        if (hitSide == Direction.DOWN) {
            attachToBottom(player, blockPos);
            attached = true;
        } else if (hitSide.getAxis().isHorizontal()) {
            attachToSide(player, blockPos, hitSide);
            attached = true;
        }

        if (attached && !player.getWorld().isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            player.getWorld().getServer().execute(() -> {
                ModPacketsS2CServer.sendBatAttachState(serverPlayer, isAttached, attachType.ordinal(),
                        attachedBlockPos, attachedSide);
                ModPacketsS2CServer.broadcastBatAttachState(serverPlayer, isAttached, attachType.ordinal(),
                        attachedBlockPos, attachedSide);
            });
        }

        return attached;
    }

    private void attachToSide(PlayerEntity player, BlockPos blockPos, Direction side) {
        isAttached = true;
        attachType = AttachType.SIDE;
        attachedBlockPos = blockPos;
        attachedSide = side;

        Vec3d attachPos = Vec3d.ofCenter(blockPos).add(Vec3d.of(side.getVector()).multiply(0.75d)).add(0, -0.5, 0);
        player.setPosition(attachPos.x, attachPos.y, attachPos.z);
        player.setVelocity(Vec3d.ZERO);
        player.setOnGround(true);
        player.velocityDirty = true;
        player.velocityModified = true;

        AnimUtils.playPowerAnimLoop(player, AnimRegistries.POWER_ANIM_ATTACH_SIDE, AnimUtils.AnimationSendSideType.ONLY_SERVER);
    }

    private void attachToBottom(PlayerEntity player, BlockPos blockPos) {
        isAttached = true;
        attachType = AttachType.BOTTOM;
        attachedBlockPos = blockPos;
        attachedSide = Direction.DOWN;
        bottomAttachTimer = 0;

        Vec3d attachPos = Vec3d.ofCenter(blockPos).add(0, -1.5f, 0);
        player.setPosition(attachPos.x, attachPos.y, attachPos.z);
        player.setVelocity(Vec3d.ZERO);
        player.setOnGround(true);
        player.velocityDirty = true;
        player.velocityModified = true;

        AnimUtils.playPowerAnimLoop(player, AnimRegistries.POWER_ANIM_ATTACH_BOTTOM, AnimUtils.AnimationSendSideType.ONLY_SERVER);
    }

    public void detach(PlayerEntity player, boolean isByJump) {
        if (!isAttached) {
            return;
        }

        if (attachType == AttachType.SIDE && sideAttachAction != null) {
            sideAttachAction.accept(new EntityActionContext(getHolder(), getHolder().getPos()));
        }

        isAttached = false;
        attachType = AttachType.NONE;
        attachedBlockPos = null;
        attachedSide = null;
        bottomAttachTimer = 0;

        player.setOnGround(false);
        player.setVelocity(Vec3d.ZERO);
        player.addVelocity(0, 0.4f, 0);
        if (isByJump) {
            float yaw = player.getYaw();
            double yawRadians = Math.toRadians(yaw);

            double dirX = -Math.sin(yawRadians);
            double dirZ = Math.cos(yawRadians);

            double jumpSpeed = 1.25f;
            player.addVelocity(dirX * jumpSpeed, 0.4f, dirZ * jumpSpeed);
        }
        player.velocityDirty = true;
        player.velocityModified = true;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModPacketsS2CServer.sendBatAttachState(serverPlayer, false, AttachType.NONE.ordinal(), null, null);
            ModPacketsS2CServer.broadcastBatAttachState(serverPlayer, false, AttachType.NONE.ordinal(), null, null);
        }

        AnimUtils.stopPowerAnimWithIDs(player, AnimUtils.AnimationSendSideType.ONLY_SERVER, AnimRegistries.POWER_ANIM_ATTACH_SIDE, AnimRegistries.POWER_ANIM_ATTACH_BOTTOM);
    }

    private void maintainAttachPosition(PlayerEntity player) {
        if (attachedBlockPos == null || attachedSide == null) {
            return;
        }

        Vec3d targetPos;
        if (attachType == AttachType.SIDE) {
            targetPos = Vec3d.ofCenter(attachedBlockPos).add(Vec3d.of(attachedSide.getVector()));
        } else {
            targetPos = Vec3d.ofCenter(attachedBlockPos).add(0, -1.5f, 0);
        }

        player.setVelocity(0, 0, 0);
        player.setPosition(targetPos.x, targetPos.y, targetPos.z);
        player.slowMovement(player.getBlockStateAtPos(), new Vec3d(0.0, 0.0, 0.0));
        player.setOnGround(true);
        player.fallDistance = 0;
        player.horizontalSpeed = 0;
        player.distanceTraveled = 0;
        player.speed = 0;
        player.sidewaysSpeed = 0;
        player.upwardSpeed = 0;
        player.forwardSpeed = 0;
        player.velocityDirty = true;
        player.velocityModified = true;
    }

    private void maintainFacingDirection(PlayerEntity player) {
        if (attachedSide == null) {
            return;
        }

        float targetYaw = getTargetYaw();
        player.setBodyYaw(targetYaw);
        player.prevBodyYaw = targetYaw;
    }

    public float getTargetYaw() {
        if (attachedSide == null) {
            return getHolder().getYaw();
        }
        return switch (attachedSide) {
            case NORTH -> 0.0f;
            case SOUTH -> 180.0f;
            case WEST -> -90.0f;
            case EAST -> 90.0f;
            default -> getHolder().getYaw();
        };
    }

    public boolean isAttached() {
        return isAttached;
    }

    public AttachType getAttachType() {
        return attachType;
    }

    public BlockPos getAttachedBlockPos() {
        return attachedBlockPos;
    }

    public Direction getAttachedSide() {
        return attachedSide;
    }

    public void handleJump(PlayerEntity player) {
        if (isAttached) {
            detach(player, true);
        }
    }

    public void handleRightClick(PlayerEntity player) {
        if (isAttached) {
            detach(player, false);
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("bat_block_attach"));
    }

    public static PowerConfiguration<BatBlockAttachPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}