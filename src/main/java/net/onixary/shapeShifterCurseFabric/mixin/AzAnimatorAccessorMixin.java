package net.onixary.shapeShifterCurseFabric.mixin;

import mod.azure.azurelib.rewrite.animation.AzAnimator;
import mod.azure.azurelib.rewrite.animation.AzAnimatorAccessor;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class AzAnimatorAccessorMixin implements AzAnimatorAccessor<PlayerEntity> {
    @Unique
    private AzAnimator<PlayerEntity> azAnimator;

    @Override
    @Nullable
    public AzAnimator<PlayerEntity> getAnimatorOrNull() {
        return azAnimator;
    }

    @Override
    public void setAnimator(AzAnimator<PlayerEntity> animator) {
        this.azAnimator = animator;
    }
}
