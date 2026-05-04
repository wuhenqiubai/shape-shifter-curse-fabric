package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.blocks.TempWebBridgeBlock;
import net.onixary.shapeShifterCurseFabric.entity.projectile.WebBullet;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class WebBridgeAction {

    public record WebLadderConfig(int SideBlockNum, int BottomBlockNum, int TopBlockNum,
                                   boolean LargerLadder, float LargerLadderCountPercent) {}
    public record WebBridgeConfig(int Length, int Width) {}

    private static boolean setWebBlock(World world, BlockPos pos, Block webBlock, Direction facing) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir() || blockState.isOf(webBlock)) {
            world.setBlockState(pos, webBlock.getDefaultState().with(TempWebBridgeBlock.HORIZONTAL_FACING, facing));
            return true;
        }
        return false;
    }

    public static class WebBridgeEntityAction extends EntityActionType {
        public static final TypedDataObjectFactory<WebBridgeEntityAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("web_bridge_length", SerializableDataTypes.INT, 16)
                                .add("web_bridge_width", SerializableDataTypes.INT, 0),
                        data -> new WebBridgeEntityAction(data.getInt("web_bridge_length"), data.getInt("web_bridge_width")),
                        (action, sd) -> sd.instance()
                );

        private final int length, width;

        public WebBridgeEntityAction(int length, int width) {
            this.length = length;
            this.width = width;
        }

        @Override
        public void accept(EntityActionContext context) {
            BlockPos pos = context.entity().getBlockPos();
            Direction direction = context.entity().getHorizontalFacing();
            buildBridge(context.entity().getWorld(), pos, direction, length, width);
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("web_bridge"), DATA_FACTORY);
        }
    }

    public static class FireWebBulletAction extends EntityActionType {
        public static final TypedDataObjectFactory<FireWebBulletAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("tier", SerializableDataTypes.INT, 1)
                                .add("divergence", SerializableDataTypes.FLOAT, 1F)
                                .add("speed", SerializableDataTypes.FLOAT, 1.5F)
                                .add("projectile_action", EntityAction.DATA_TYPE.optional(), Optional.empty()),
                        data -> new FireWebBulletAction(
                                data.getInt("tier"), data.getFloat("divergence"),
                                data.getFloat("speed"), data.get("projectile_action")),
                        (action, sd) -> sd.instance()
                );

        private final int tier;
        private final float divergence, speed;
        private final Optional<EntityAction> projectileAction;

        public FireWebBulletAction(int tier, float divergence, float speed, Optional<EntityAction> projectileAction) {
            this.tier = tier;
            this.divergence = divergence;
            this.speed = speed;
            this.projectileAction = projectileAction;
        }

        @Override
        public void accept(EntityActionContext context) {
            if (!(context.entity() instanceof LivingEntity livingEntity)) return;
            WebBullet bullet = new WebBullet(livingEntity, tier);
            bullet.setVelocity(livingEntity, livingEntity.getPitch(), livingEntity.getYaw(), 0.0f, speed, divergence);
            livingEntity.getWorld().spawnEntity(bullet);
            projectileAction.ifPresent(a -> a.accept(new EntityActionContext(bullet)));
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("fire_web_bullet"), DATA_FACTORY);
        }
    }

    private static void buildBridge(World world, BlockPos pos, Direction direction, int length, int width) {
        if (direction == Direction.UP || direction == Direction.DOWN) return;
        Random random = world.getRandom();
        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        Block block = RegCustomBlock.TEMP_WEB_BRIDGE;

        for (int k = -width; k <= width; k++)
            for (int m = -width; m <= width; m++)
                setWebBlock(world, pos.add(k, 0, m), block, dirs[random.nextInt(4)]);

        BlockPos now = pos;
        for (int i = 0; i < length; i++) {
            setWebBlock(world, now, block, dirs[random.nextInt(4)]);

            BlockPos tp = now;
            Direction td = direction.rotateYClockwise();
            for (int j = 0; j < width; j++) {
                tp = tp.offset(td);
                setWebBlock(world, tp, block, dirs[random.nextInt(4)]);
            }

            tp = now;
            td = direction.rotateYCounterclockwise();
            for (int j = 0; j < width; j++) {
                tp = tp.offset(td);
                setWebBlock(world, tp, block, dirs[random.nextInt(4)]);
            }

            now = now.offset(direction);
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerAction(Consumer<ActionConfiguration<? extends EntityActionType>> actionReg,
                                       Consumer<ActionConfiguration<? extends BiEntityActionType>> biActionReg) {
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("web_bridge"), WebBridgeEntityAction.DATA_FACTORY));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("fire_web_bullet"), FireWebBulletAction.DATA_FACTORY));
    }
}