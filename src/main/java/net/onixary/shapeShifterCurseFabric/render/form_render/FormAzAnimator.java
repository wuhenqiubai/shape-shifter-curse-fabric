package net.onixary.shapeShifterCurseFabric.render.form_render;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

public class FormAzAnimator extends AzEntityAnimator<PlayerEntity> {
    public FormAzAnimator() {
        super(AzAnimatorConfig.builder().build());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<PlayerEntity> container) {}

    @Override
    public @NotNull Identifier getAnimationLocation(PlayerEntity player) {
        return ShapeShifterCurseFabric.identifier("missing");
    }
}
