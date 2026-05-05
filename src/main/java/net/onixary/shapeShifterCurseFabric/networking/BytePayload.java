package net.onixary.shapeShifterCurseFabric.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Generic byte-buffer payload for migration from old Identifier+PacketByteBuf API
 * to Fabric 1.21 CustomPayload API.
 * Each packet ID still uses an Identifier; the payload wraps the raw buf.
 */
public record BytePayload(Id<BytePayload> id, PacketByteBuf data) implements CustomPayload {

    private static final ConcurrentHashMap<Identifier, Id<BytePayload>> IDS = new ConcurrentHashMap<>();

    public static Id<BytePayload> id(Identifier identifier) {
        return IDS.computeIfAbsent(identifier, id -> new Id<>(id));
    }

    public static final PacketCodec<PacketByteBuf, BytePayload> CODEC = PacketCodec.of(
        (buf, payload) -> buf.writeBytes(payload.data.copy()),
        buf -> new BytePayload(id(Identifier.of("unused", "dynamic")), new PacketByteBuf(buf.copy()))
    );

    /** Shorthand: register + send */
    public static void registerS2C(Identifier identifier) {
        PayloadTypeRegistry.playS2C().register(id(identifier), CODEC);
    }

    public static void registerC2S(Identifier identifier) {
        PayloadTypeRegistry.playC2S().register(id(identifier), CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() { return id; }
}
