package net.onixary.shapeShifterCurseFabric.mixin;


import io.github.apace100.apoli.component.PowerHolderComponent;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import mod.azure.azurelib.common.internal.common.cache.object.GeoBone;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.NoRenderArmPower;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form_render.*;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

// issue: OverrideSkinFirstPersonMixin会与某些其他mod不兼容，需要寻找原因所在
@Mixin(PlayerEntityRenderer.class)
public abstract class OverrideSkinFirstPersonMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    protected OverrideSkinFirstPersonMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowSize) {
        super(ctx, model, shadowSize);
    }

    // 自定义皮肤路径
    @Unique
    private static final Identifier CUSTOM_SKIN = Identifier.of(ShapeShifterCurseFabric.MOD_ID, "textures/entity/base_player/ssc_base_skin.png");

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true)
    private void shape_shifter_curse$RenderArm_HEAD(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)) {return;}  // 仅当玩家激活Mod后才进行修改
        if (!ShapeShifterCurseFabric.clientConfig.ignoreNoRenderArmPower && PowerHolderComponent.hasPower(player, NoRenderArmPower.class)) {  // 不渲染手臂情况
            ci.cancel();
        }
    }

    @Inject(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;setModelPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V", shift = At.Shift.AFTER))
    private void shape_shifter_curse$RenderArm_setModelPose_AFTER(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        // 渲染变身模型-根据模型设置修改手臂组件渲染
        if (RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)) {return;}  // 仅当玩家激活Mod后才进行修改
        if (!ShapeShifterCurseFabric.clientConfig.enableFormModelOnVanillaFirstPersonRender) {return;}  // 仅当启用自定义第一人称渲染时才进行修改
        for (OriginalFurClient.OriginFur fur : ((IPlayerEntityMixins) player).originalFur$getCurrentFurs()) {
            OriginFurModel OFModel = (OriginFurModel) fur.getGeoModel();
            boolean IsRenderRight = arm.equals(this.getModel().rightArm);
            // 设置手臂组件是否显示
            if (IsRenderRight) {
                arm.visible = !OFModel.hiddenParts.contains(OriginFurModel.VMP.rightArm);
                sleeve.visible = !OFModel.hiddenParts.contains(OriginFurModel.VMP.rightSleeve);
            }
            else {
                arm.visible = !OFModel.hiddenParts.contains(OriginFurModel.VMP.leftArm);
                sleeve.visible = !OFModel.hiddenParts.contains(OriginFurModel.VMP.leftSleeve);
            }
        }
    }

    @Inject(method = "renderArm", at = @At("RETURN"))
    private void shape_shifter_curse$RenderArm_RETURN(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        // 渲染变身模型
        if (RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)) {return;}  // 仅当玩家激活Mod后才进行修改
        if (!ShapeShifterCurseFabric.clientConfig.enableFormModelOnVanillaFirstPersonRender) {return;}  // 仅当启用自定义第一人称渲染时才进行修改
        boolean IsRenderRight = arm.equals(this.getModel().rightArm);
        String GeoBoneName = IsRenderRight ? "bipedRightArm" : "bipedLeftArm";
        for (OriginalFurClient.OriginFur fur : ((IPlayerEntityMixins) player).originalFur$getCurrentFurs()) {
            if (fur == null) {return;}
            Origin origin = fur.currentAssociatedOrigin;
            if (origin == null) {return;}
            PlayerEntityRenderer EntityRender = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
            OriginFurModel OFModel = (OriginFurModel) fur.getGeoModel();
            OriginFurAnimatable OFAnimatable = fur.getAnimatable();
            Optional<GeoBone> OptionalGeoBone = OFModel.getBone(GeoBoneName);
            if (OptionalGeoBone.isEmpty()) {
                // 有时AzureLib 未能及时注册 GeoBone 因此需要手动注册
                if (OFModel.getAnimationProcessor().getRegisteredBones().isEmpty()) {
                    ShapeShifterCurseFabric.LOGGER.info("GeoBone 未注册, 尝试重新注册模型");
                    BakedGeoModel bakedGeoModel = OFModel.getBakedModel(OFModel.getModelResource(OFAnimatable));
                    OFModel.getAnimationProcessor().setActiveModel(bakedGeoModel);
                }
                return;
            }
            var eRA = (IPlayerEntityMixins) EntityRender;
            var acc = (ModelRootAccessor) EntityRender.getModel();
            OFModel.preprocess(origin, EntityRender, eRA, acc, player);
            GeoBone geoBone = OptionalGeoBone.get();
            fur.setPlayer(player);
            OFModel.setPlayer(player);
            matrices.push();
            matrices.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
            matrices.translate(0, -1.51f, 0);
            OFModel.resetBone(GeoBoneName);
            OFModel.translatePositionForBone(GeoBoneName, ((IMojModelPart) (Object) arm).originfurs$getPosition());
            OFModel.translatePositionForBone(GeoBoneName, new Vec3d(5 * (IsRenderRight ? -1.0 : 1.0), 2, 0));
            OFModel.setRotationForBone(GeoBoneName, ((IMojModelPart) (Object) arm).originfurs$getRotation());
            OFModel.invertRotForPart(GeoBoneName, false, true, true);
            RenderLayer renderLayerNormal = RenderLayer.getEntityTranslucent(OFModel.getTextureResource(OFAnimatable));
            // TODO: RenderOFModelBone disabled - AzureLib 3.0.25 API migration needed
            // this.RenderOFModelBone(fur, geoBone, matrices, OFAnimatable, vertexConsumers, renderLayerNormal, vertexConsumers.getBuffer(renderLayerNormal), light);
            // RenderLayer renderLayerFullBright = RenderLayer.getEntityTranslucent(OFModel.getFullbrightTextureResource(OFAnimatable));
            // this.RenderOFModelBone(fur, geoBone, matrices, OFAnimatable, vertexConsumers, renderLayerFullBright, vertexConsumers.getBuffer(renderLayerFullBright), Integer.MAX_VALUE - 1);
            matrices.pop();
            // TODO: Render Overlay - OverlayTexture.packUv/ModelPart.render API changed in 1.21 + AzureLib
        }
    }
    // fur.renderBone - AzureLib 3.0.25 preRender/renderRecursively/postRender API changed, needs rewrite
    /* TODO: AzureLib 3.0.25 GeoObjectRenderer API migration needed
    @Unique
    private void RenderOFModelBone(...) { ... }
    */


    // TODO: getSkinTexture may have been renamed in 1.21
    /* @Redirect(method="renderArm", at= @At(value="INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinTexture()Lnet/minecraft/util/Identifier;"))
    private Identifier shape_shifter_curse$getSkinTexture(AbstractClientPlayerEntity player) {
        if (!RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE))
        {
            if (!RegPlayerSkinComponent.SKIN_SETTINGS.get(player).shouldKeepOriginalSkin()) {
                return CUSTOM_SKIN;
            }
        }
        return player.getSkinTexture();
    } */
}
