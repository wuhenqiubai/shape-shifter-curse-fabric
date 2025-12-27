package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 补全apoli缺失的访问器
 * 用于获取、设置mining等私有字段
 */
@Mixin(ServerPlayerInteractionManager.class)
public interface ServerPlayerInteractionManagerAccessor {
    @Accessor("mining")
    boolean getMining();

    @Accessor("mining")
    void setMining(boolean mining);

    @Accessor("miningPos")
    BlockPos getMiningPos();
}