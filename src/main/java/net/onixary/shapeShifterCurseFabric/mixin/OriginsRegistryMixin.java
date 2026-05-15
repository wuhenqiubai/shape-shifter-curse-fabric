package net.onixary.shapeShifterCurseFabric.mixin;

import com.google.gson.JsonParser;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.onixary.shapeShifterCurseFabric.integration.origins.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginEvents;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginalFurClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Pseudo
@Mixin(OriginRegistry.class)
public class OriginsRegistryMixin {

    //@Inject(method="register(Lnet/minecraft/util/Identifier;Lio/github/apace100/origins/origin/Origin;)Lio/github/apace100/origins/origin/Origin;", at=@At("RETURN"))
    @Unique
    private static void registerMixin(Identifier id, Origin origin, CallbackInfoReturnable<Origin> cir){
        OriginEvents.ORIGIN_REGISTRY_ADDED_EVENT.invoker().onOriginAddedToRegistry(origin,id);
    }

    @Mixin(value = ModPacketsS2C.class, remap = false)
    public static class OriginListMixin$ORIF{
        @Inject(method = "receiveOriginList", at = @At("RETURN"))
        private static void onReceivedOriginList(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender, CallbackInfo ci) {
            // 在接收完起源列表后，为每个起源注册毛皮资源
            OriginRegistry.identifiers().forEach(id -> {
                var origin = OriginRegistry.get(id);
                if (origin != null) {
                    var manager = MinecraftClient.getInstance().getResourceManager();
                    String path = "furs";
                    var fur = OriginalFurClient.FUR_RESOURCES.getOrDefault(id, null);
                    try {
                        if (fur == null) {
                            OriginalFurClient.FUR_REGISTRY.put(id, new OriginalFurClient.OriginFur(JsonParser.parseString("{}").getAsJsonObject()));
                        } else {
                            OriginalFurClient.FUR_REGISTRY.put(id, new OriginalFurClient.OriginFur(JsonParser.parseString(new String(fur.getInputStream().readAllBytes())).getAsJsonObject()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
