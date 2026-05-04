package net.onixary.shapeShifterCurseFabric.integration.origins.networking;

import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.minecraft.util.Identifier;

public class ModPackets {

    public static final Identifier HANDSHAKE = Origins.identifier("handshake");

    public static final Identifier OPEN_ORIGIN_SCREEN = Identifier.of(Origins.MODID, "open_origin_screen");
    public static final Identifier CHOOSE_ORIGIN = Identifier.of(Origins.MODID, "choose_origin");
    public static final Identifier USE_ACTIVE_POWERS = Identifier.of(Origins.MODID, "use_active_powers");
    public static final Identifier ORIGIN_LIST = Identifier.of(Origins.MODID, "origin_list");
    public static final Identifier LAYER_LIST = Identifier.of(Origins.MODID, "layer_list");
    public static final Identifier POWER_LIST = Identifier.of(Origins.MODID, "power_list");
    public static final Identifier CHOOSE_RANDOM_ORIGIN = Identifier.of(Origins.MODID, "choose_random_origin");
    public static final Identifier CONFIRM_ORIGIN = Origins.identifier("confirm_origin");
    public static final Identifier PLAYER_LANDED = Origins.identifier("player_landed");
    public static final Identifier BADGE_LIST = Origins.identifier("badge_list");
}
