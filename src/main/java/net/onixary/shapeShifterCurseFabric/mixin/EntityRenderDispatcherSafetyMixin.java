package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Safeguard: prevents NPE when EntityRenderDispatcher has a null renderer
 * (can happen during form transitions or renderer cache mismatches).
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherSafetyMixin {

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Redirect(method = "shouldRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;shouldRender(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Frustum;DDD)Z"))
	private boolean preventNullRendererShouldRender(EntityRenderer instance, net.minecraft.entity.Entity entity, Frustum frustum, double x, double y, double z) {
		if (instance == null) return false;
		return instance.shouldRender(entity, frustum, x, y, z);
	}
}
