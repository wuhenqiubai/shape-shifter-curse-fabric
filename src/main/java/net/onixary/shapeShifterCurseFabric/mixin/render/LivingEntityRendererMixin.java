package net.onixary.shapeShifterCurseFabric.mixin.render;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.integration.EMFIntegration;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.render.form_render.FormRenderFeature;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    // SSC 动画文件名中需要保护的关键词（不被 FA 覆盖）
    @Unique
    private static final java.util.Set<String> SSC_SPECIAL = java.util.Set.of(
        "crawl", "swim", "jump", "rush", "flight", "transform",
        "attack", "dig", "climb", "float", "fall", "sleep", "attach", "ride"
    );

    @Unique
    private static boolean isSscSpecialAnim(String animId) {
        if (animId == null) return false;
        String lower = animId.toLowerCase();
        for (String kw : SSC_SPECIAL) {
            if (lower.contains(kw)) return true;
        }
        return false;
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void onRenderHead(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player && (Object) this instanceof PlayerEntityRenderer) {
            if (FormTextureUtils.getPlayerForm_Render(player).getBodyType() == PlayerFormBodyType.FERAL) {
                EMFIntegration.pauseAllAnimations(player);
            } else {
                String animId = AnimSystem.getCurrentAnimId(player);
                if (isSscSpecialAnim(animId)) {
                    EMFIntegration.pauseAllAnimations(player);
                }
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V", shift = At.Shift.BEFORE))
    private void renderPreProcessMixin(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity && (Object) this instanceof PlayerEntityRenderer playerEntityRenderer) {
            FormRenderFeature.rM_PartA(playerEntityRenderer, abstractClientPlayerEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V", shift = At.Shift.AFTER))
    private void renderOverlayTexture(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity && (Object) this instanceof PlayerEntityRenderer playerEntityRenderer) {
            FormRenderFeature.rM_PartB(playerEntityRenderer, abstractClientPlayerEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("RETURN"))
    private void onRenderReturn(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player) {
            EMFIntegration.resumeAnimations(player);
        }
    }
}
