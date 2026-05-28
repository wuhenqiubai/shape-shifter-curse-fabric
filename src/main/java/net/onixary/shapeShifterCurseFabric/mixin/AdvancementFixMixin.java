package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.onixary.shapeShifterCurseFabric.util.AdvancementUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AdvancementManager.class)
public class AdvancementFixMixin {
	@ModifyVariable(method = "tryAdd", at = @At("HEAD"), argsOnly = true)
	private AdvancementEntry patchAdvancement(AdvancementEntry entry) {
		return AdvancementUtils.applyPendingPatches(entry);
    }
}