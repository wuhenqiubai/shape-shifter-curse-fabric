package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

public class MustCrawlingCondition extends EntityConditionType {

    public static final TypedDataObjectFactory<MustCrawlingCondition> DATA_FACTORY =
            TypedDataObjectFactory.simple(
                    new SerializableData(),
                    data -> new MustCrawlingCondition(),
                    (c, sd) -> sd.instance()
            );

    private static boolean isHeadNotCollide(Entity entity) {
        if (entity.noClip || entity.isSpectator()) return true;
        BlockPos up1pos = entity.getBlockPos().up();
        BlockState up1block = entity.getWorld().getBlockState(up1pos);
        if (up1block.getBlock() instanceof ScaffoldingBlock) return true;
        Vec3d collideTestPoint = entity.getPos().add(0f, 1.5f, 0f);
        BlockHitResult hitResult = up1block.getCollisionShape(entity.getWorld(), up1pos)
                .raycast(entity.getPos(), collideTestPoint, up1pos);
        return hitResult == null || hitResult.getType() == BlockHitResult.Type.MISS;
    }

    @Override
    public boolean test(EntityConditionContext ctx) {
        return !isHeadNotCollide(ctx.entity());
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("must_crawling"), DATA_FACTORY);
    }
}