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
    private static final java.util.HashSet<Identifier> REGISTERED_S2C = new java.util.HashSet<>();
    private static final java.util.HashSet<Identifier> REGISTERED_C2S = new java.util.HashSet<>();

    public static Id<BytePayload> id(Identifier identifier) {
        return IDS.computeIfAbsent(identifier, id -> new Id<>(id));
    }

    /** Create a per-ID CODEC whose decoder returns the correct Id */
    private static PacketCodec<PacketByteBuf, BytePayload> codecFor(Id<BytePayload> pid) {
        return PacketCodec.of(
            (payload, buf) -> buf.writeBytes(payload.data.readBytes(payload.data.readableBytes())),
            buf -> new BytePayload(pid, new PacketByteBuf(buf.readBytes(buf.readableBytes())))
        );
    }

    /** Shorthand: register S2C (idempotent) */
    public static void registerS2C(Identifier identifier) {
        if (REGISTERED_S2C.add(identifier)) {
            PayloadTypeRegistry.playS2C().register(id(identifier), codecFor(id(identifier)));
        }
    }

    /** Shorthand: register C2S (idempotent) */
    public static void registerC2S(Identifier identifier) {
        if (REGISTERED_C2S.add(identifier)) {
            PayloadTypeRegistry.playC2S().register(id(identifier), codecFor(id(identifier)));
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() { return id; }
}
