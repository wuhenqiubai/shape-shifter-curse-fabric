package net.onixary.shapeShifterCurseFabric.networking;

import static net.onixary.shapeShifterCurseFabric.networking.ModPackets.*;

/**
 * Server-safe S2C payload type registration.
 * No client dependencies — safe to call from the common entrypoint.
 */
public class ModPacketsInit {

	public static void registerS2CPayloads() {
		BytePayload.registerS2C(SYNC_CURSED_MOON_DATA);
		BytePayload.registerS2C(SYNC_FORM_CHANGE);
		BytePayload.registerS2C(TRANSFORM_EFFECT_ID);
		BytePayload.registerS2C(INSTINCT_THRESHOLD_EFFECT_ID);
		BytePayload.registerS2C(SYNC_TRANSFORM_STATE);
		BytePayload.registerS2C(SYNC_BAT_ATTACH_STATE);
		BytePayload.registerS2C(UPDATE_OVERLAY_EFFECT);
		BytePayload.registerS2C(UPDATE_OVERLAY_FADE_EFFECT);
		BytePayload.registerS2C(TRANSFORM_COMPLETE_EFFECT);
		BytePayload.registerS2C(RESET_FIRST_PERSON);
		BytePayload.registerS2C(SYNC_OTHER_PLAYER_BAT_ATTACH_STATE);
		BytePayload.registerS2C(SYNC_FORCE_SNEAK_STATE);
		BytePayload.registerS2C(REMOVE_DYNAMIC_FORM_EXCEPT);
		BytePayload.registerS2C(UPDATE_DYNAMIC_FORM);
		BytePayload.registerS2C(LOGIN_PACKET);
		BytePayload.registerS2C(UPDATE_PATRON_LEVEL);
		BytePayload.registerS2C(OPEN_PATRON_FORM_SELECT_MENU);
		BytePayload.registerS2C(OPEN_FORM_SELECT_MENU);
		BytePayload.registerS2C(ACTIVE_VIRTUAL_TOTEM);
		BytePayload.registerS2C(UPDATE_POWER_ANIM_DATA_TO_CLIENT);
	}
}
