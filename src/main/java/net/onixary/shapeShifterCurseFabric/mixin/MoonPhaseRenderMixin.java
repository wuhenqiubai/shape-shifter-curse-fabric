package net.onixary.shapeShifterCurseFabric.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

@Mixin(value = WorldRenderer.class, priority = 949)
public class MoonPhaseRenderMixin {
    @Unique
    private final Identifier Vanilla_MOON_PHASES = Identifier.of("textures/environment/moon_phases.png");

    @Unique
    private final Identifier CURSED_MOON_PHASES = Identifier.of(MOD_ID, "textures/environment/cursed_moon_phases.png");

    @Unique
    public Identifier getMoonIdentifier() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            return CursedMoon.isCursedMoon(client.world) ? CURSED_MOON_PHASES : Vanilla_MOON_PHASES;
        }
        // fallback to client state
        return CursedMoon.clientIsCursedMoon ? CURSED_MOON_PHASES : Vanilla_MOON_PHASES;
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 1))
    private Identifier getMoonPhaseTexture(Identifier identifier) {
        Identifier moonId = getMoonIdentifier();
        // 防止返回 null 导致崩溃
        return moonId != null ? moonId : identifier;
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 1, shift = At.Shift.BEFORE))
    private void setMoonPurpleTint(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null && CursedMoon.isCursedMoon(client.world)) {
            RenderSystem.setShaderColor(1.0F, 0.24F, 0.82F, 1.0F);
        }
    }
}
