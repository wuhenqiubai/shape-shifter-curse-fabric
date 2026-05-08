package net.onixary.shapeShifterCurseFabric.integration.origins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.onixary.shapeShifterCurseFabric.integration.origins.OriginsClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ConnectScreen.class)
public class ServerCheckMixin {

    @Inject(method = "connect*", at = @At("HEAD"))
    private void resetServerOriginsState(MinecraftClient client, ServerAddress address, ServerInfo info, CallbackInfo ci) {
        OriginsClient.isServerRunningOrigins = false;
    }
}
