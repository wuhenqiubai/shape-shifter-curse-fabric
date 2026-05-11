package net.onixary.shapeShifterCurseFabric.integration.origins.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.onixary.shapeShifterCurseFabric.integration.origins.power.ModifyTypeTagPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts {@link EntityType#isIn(TagKey)} to also check
 * {@link ModifyTypeTagPower}. The entity context is provided via
 * {@link ModifyTypeTagPower#CURRENT_ENTITY} ThreadLocal, which is
 * set by {@link InTagConditionMixin} before the tag check.
 */
@Mixin(EntityType.class)
public abstract class ModifyTypeTagMixin {

    @Inject(method = "isIn", at = @At("RETURN"), cancellable = true)
    private void calio$modifyIsIn(TagKey<?> tagKey, CallbackInfoReturnable<Boolean> cir) {
        if (!tagKey.isOf(RegistryKeys.ENTITY_TYPE)) {
            return;
        }
        if (cir.getReturnValue()) {
            return;
        }

        var entity = ModifyTypeTagPower.CURRENT_ENTITY.get();
        if (entity != null) {
            @SuppressWarnings("unchecked")
            TagKey<EntityType<?>> entityTag = (TagKey<EntityType<?>>) tagKey;
            if (ModifyTypeTagPower.isEntityInTag(entity.getId(), entityTag)) {
                cir.setReturnValue(true);
            }
        }
    }
}
