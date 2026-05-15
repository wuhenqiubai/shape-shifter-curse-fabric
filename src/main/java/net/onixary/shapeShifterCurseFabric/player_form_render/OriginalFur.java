package net.onixary.shapeShifterCurseFabric.player_form_render;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.networking.BytePayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class OriginalFur implements ModInitializer {
    public static final String[] QUIVER_MODS = new String[]{
            "nyfsquiver"
    };
    public static final String[] BACKPACK_MODS = new String[]{
            "travelersbackpack", "umu_backpack", "packedup"
    };
    @Override
    public void onInitialize() {
        BytePayload.registerC2S(C2S_REQ_ORIGIN_UUID);
        ServerPlayNetworking.registerGlobalReceiver(BytePayload.id(C2S_REQ_ORIGIN_UUID), (payload, context) -> {
            var buf = payload.data();
            RequestOriginPacket packet = new RequestOriginPacket();
            packet.read(buf);

	        var server = context.server();
	        if (server == null) {
		        ShapeShifterCurseFabric.LOGGER.warn("Server is null when handling origin request");
		        return;
	        }

            var rqPlayer = server.getPlayerManager().getPlayer(packet.requestedPlayerUUID);
            if (rqPlayer != null){
                ArrayList<Identifier> ids = new ArrayList<>();
                ((IPlayerEntityMixins)rqPlayer).originalFur$currentOrigins().forEach(origin -> ids.add(origin.getIdentifier()));
                RequestOriginPacket response = new RequestOriginPacket();
                response.origins = ids;
                response.requestedPlayerUUID = packet.requestedPlayerUUID;
                response.requestedPlayerName = rqPlayer.getName().getString();
            } else {
	            ShapeShifterCurseFabric.LOGGER.warn("Player not found for UUID: {}", packet.requestedPlayerUUID);
            }
        });
    }
    @NotNull public static final Identifier S2C_REQ_ORIGIN_RESP = Identifier.of("orif", "origin_response");
    @NotNull public static final Identifier C2S_REQ_ORIGIN_UUID = Identifier.of("orif", "request_player_origin");
    public static class RequestOriginPacket {
        public UUID requestedPlayerUUID = new UUID(0,0);
        public String requestedPlayerName = "";
        public ArrayList<Identifier> origins = new ArrayList<>();
        public void writeSv(PacketByteBuf buf) {
            buf.writeUuid(requestedPlayerUUID);
            buf.writeString(requestedPlayerName);
            buf.writeInt(origins.size());
            origins.forEach(identifier -> {
                buf.writeString(identifier.toString());
            });
        }
        public void read(PacketByteBuf buf){
            requestedPlayerUUID = buf.readUuid();
            requestedPlayerName = buf.readString();
            var sz = buf.readInt();
            origins = new ArrayList<>();
            for (int i = 0; i < sz; i++){
                origins.add(Identifier.tryParse(buf.readString()));
            };
        }
    }
}
