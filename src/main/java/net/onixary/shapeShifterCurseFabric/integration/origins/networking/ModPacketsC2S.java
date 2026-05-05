package net.onixary.shapeShifterCurseFabric.integration.origins.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.component.OriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayer;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayers;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.onixary.shapeShifterCurseFabric.networking.BytePayload;

import java.util.List;
import java.util.Random;

public class ModPacketsC2S {

    public static void register() {
        BytePayload.registerC2S(ModPackets.CHOOSE_ORIGIN);
        BytePayload.registerC2S(ModPackets.CHOOSE_RANDOM_ORIGIN);
        BytePayload.registerS2C(ModPackets.ORIGIN_LIST);
        BytePayload.registerS2C(ModPackets.LAYER_LIST);
        BytePayload.registerS2C(ModPackets.OPEN_ORIGIN_SCREEN);
        BytePayload.registerS2C(ModPackets.CONFIRM_ORIGIN);

        ServerPlayNetworking.registerGlobalReceiver(BytePayload.id(ModPackets.CHOOSE_ORIGIN), (payload, context) -> {
            chooseOrigin(context.player(), payload.data());
        });
        ServerPlayNetworking.registerGlobalReceiver(BytePayload.id(ModPackets.CHOOSE_RANDOM_ORIGIN), (payload, context) -> {
            chooseRandomOrigin(context.player(), payload.data());
        });
    }

    private static void chooseOrigin(ServerPlayerEntity player, PacketByteBuf buf) {
        String originId = buf.readString(32767);
        String layerId = buf.readString(32767);
        player.getServer().execute(() -> {
            OriginComponent component = ModComponents.ORIGIN.get(player);
            OriginLayer layer = OriginLayers.getLayer(Identifier.tryParse(layerId));
            if(!component.hasAllOrigins() && !component.hasOrigin(layer)) {
                Identifier id = Identifier.tryParse(originId);
                if(id != null) {
                    Origin origin = OriginRegistry.get(id);
                    if(origin.isChoosable() && layer.contains(origin, player)) {
                        boolean hadOriginBefore = component.hadOriginBefore();
                        boolean hadAllOrigins = component.hasAllOrigins();
                        component.setOrigin(layer, origin);
                        component.checkAutoChoosingLayers(player, false);
                        component.sync();
                        if(component.hasAllOrigins() && !hadAllOrigins) {
                            OriginComponent.onChosen(player, hadOriginBefore);
                        }
                        Origins.LOGGER.info("Player " + player.getDisplayName().getString() + " chose Origin: " + originId + ", for layer: " + layerId);
                    } else {
                        Origins.LOGGER.info("Player " + player.getDisplayName().getString() + " tried to choose unchoosable Origin for layer " + layerId + ": " + originId + ".");
                        component.setOrigin(layer, Origin.EMPTY);
                    }
                    confirmOrigin(player, layer, component.getOrigin(layer));
                    component.sync();
                } else {
                    Origins.LOGGER.warn("Player " + player.getDisplayName().getString() + " chose unknown origin: " + originId);
                }
            } else {
                Origins.LOGGER.warn("Player " + player.getDisplayName().getString() + " tried to choose origin for layer " + layerId + " while having one already.");
            }
        });
    }

    private static void chooseRandomOrigin(ServerPlayerEntity player, PacketByteBuf buf) {
        String layerId = buf.readString(32767);
        player.getServer().execute(() -> {
            OriginComponent component = ModComponents.ORIGIN.get(player);
            OriginLayer layer = OriginLayers.getLayer(Identifier.tryParse(layerId));
            if(!component.hasAllOrigins() && !component.hasOrigin(layer)) {
                List<Identifier> randomOrigins = layer.getRandomOrigins(player);
                if(layer.isRandomAllowed() && randomOrigins.size() > 0) {
                    Identifier randomOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
                    Origin origin = OriginRegistry.get(randomOrigin);
                    boolean hadOriginBefore = component.hadOriginBefore();
                    boolean hadAllOrigins = component.hasAllOrigins();
                    component.setOrigin(layer, origin);
                    component.checkAutoChoosingLayers(player, false);
                    component.sync();
                    if(component.hasAllOrigins() && !hadAllOrigins) {
                        OriginComponent.onChosen(player, hadOriginBefore);
                    }
                    Origins.LOGGER.info("Player " + player.getDisplayName().getString() + " was randomly assigned the following Origin: " + randomOrigin + ", for layer: " + layerId);
                } else {
                    Origins.LOGGER.info("Player " + player.getDisplayName().getString() + " tried to choose a random Origin for layer " + layerId + ", which is not allowed!");
                    component.setOrigin(layer, Origin.EMPTY);
                }
                confirmOrigin(player, layer, component.getOrigin(layer));
                component.sync();
            } else {
                Origins.LOGGER.warn("Player " + player.getDisplayName().getString() + " tried to choose origin for layer " + layerId + " while having one already.");
            }
        });
    }

    private static void confirmOrigin(ServerPlayerEntity player, OriginLayer layer, Origin origin) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(layer.getIdentifier());
        buf.writeIdentifier(origin.getIdentifier());
        ServerPlayNetworking.send(player, new BytePayload(BytePayload.id(ModPackets.CONFIRM_ORIGIN), buf));
    }
}
