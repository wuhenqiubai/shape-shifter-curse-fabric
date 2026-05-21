package net.onixary.shapeShifterCurseFabric.networking;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnJumpPower;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnSprintingToSneakingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import net.onixary.shapeShifterCurseFabric.additional_power.JumpEventCondition;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.IPlayerAnimController;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.networking.ModPackets.*;

// 应仅在服务器端注册
// This class should only be registered on the server side
public class ModPacketsC2S {

    @FunctionalInterface
    private interface LegacyC2SReceiver {
        void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender);
    }

    private static void reg(Identifier id, LegacyC2SReceiver receiver) {
        BytePayload.registerC2S(id);
        ServerPlayNetworking.registerGlobalReceiver(BytePayload.id(id), (payload, context) -> {
            ShapeShifterCurseFabric.LOGGER.debug("[C2S] Received packet: {}", id);
            context.server().execute(() -> {
                ShapeShifterCurseFabric.LOGGER.debug("[C2S] Executing handler for: {}", id);
                receiver.receive(context.server(), context.player(), null, payload.data(), null);
            });
        });
    }

    /** Client-side only: register payload CODEC for sending (no receiver) */
    private static void regClient(Identifier id) {
        BytePayload.registerC2S(id);
    }

    /** Called from client initializer: only registers CODEC for sending */
    public static void registerClient() {
        regClient(ModPackets.VALIDATE_START_BOOK_BUTTON);
        regClient(Identifier.of(ShapeShifterCurseFabric.MOD_ID, "update_skin_setting"));
        // Add any other C2S packet IDs the client sends here
    }

    public static void register() {
        reg(ModPackets.VALIDATE_START_BOOK_BUTTON, net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::onPressStartBookButton);
        reg(Identifier.of(ShapeShifterCurseFabric.MOD_ID, "update_skin_setting"),
                (server, player, handler, buf, responseSender) -> {
                    boolean keepOriginalSkin = buf.readBoolean();
                    server.execute(() -> {
                        PlayerSkinComponent skinComp = RegPlayerSkinComponent.SKIN_SETTINGS.get(player);
                        skinComp.setKeepOriginalSkin(keepOriginalSkin);
                        // 同步到所有客户端，包括发送者自己
                        RegPlayerSkinComponent.SKIN_SETTINGS.sync(player);
                    });
                }
        );

	    reg(JUMP_DETACH_REQUEST_ID, (server, player, handler, buf, responseSender) -> server.execute(() -> {
		    BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
				    .stream()
				    .filter(BatBlockAttachPower::isAttached)
				    .findFirst()
				    .orElse(null);

		    if (attachPower != null) {
			    attachPower.handleJump(player);
		    }
	    }));

        // jump_event condition handle
        reg(JUMP_EVENT_ID, (server, player, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();

            server.execute(() -> {
                // 在服务器端设置跳跃状态
                if (player.getUuid().equals(playerUuid)) {
                    JumpEventCondition.setJumping(player, true);
                }

                PowerHolderComponent.getPowers(player, ActionOnJumpPower.class)
                        .forEach(ActionOnJumpPower::executeAction);
            });
        });

        // SPRINTING_TO_SNEAKING_EVENT condition handle
        reg(SPRINTING_TO_SNEAKING_EVENT_ID, (server, player, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();

            server.execute(() -> {
                // 在服务器端处理疾跑转潜行事件
                if (player.getUuid().equals(playerUuid)) {
                    PowerHolderComponent.getPowers(player, ActionOnSprintingToSneakingPower.class)
                            .forEach(ActionOnSprintingToSneakingPower::executeAction);
                }
            });
        });

        reg(UPDATE_CUSTOM_SETTING, net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::onUpdatePlayerCustomConfig
        );


        ServerPlayNetworking.registerGlobalReceiver(
                UPDATE_CUSTOM_COLOR,
                net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::onUpdatePlayerCustomColor
        );

        ServerPlayNetworking.registerGlobalReceiver(
                SET_PATRON_FORM,
                net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::receiveSetPatronForm
        );

        reg(SET_FORM, net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::receiveSetForm
        );

        reg(UPDATE_POWER_ANIM_DATA_TO_SERVER, ModPacketsC2S::onUpdatePowerAnimationData
        );

        reg(REQUEST_POWER_ANIM_DATA, ModPacketsC2S::onRequestPowerAnimationData
        );
    }

    private static void onPressStartBookButton(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        minecraftServer.execute(() -> {
            try {
                var component = RegPlayerFormComponent.PLAYER_FORM.get(playerEntity);
                if (component == null) return;
                if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.equals(component.getCurrentForm())) {
                    TransformManager.handleDirectTransform(playerEntity, RegPlayerForms.ORIGINAL_SHIFTER, false);
                    ShapeShifterCurseFabric.ON_ENABLE_MOD.trigger(playerEntity);
                    playerEntity.sendMessage(Text.translatable("info.shape-shifter-curse.on_enable_mod").formatted(Formatting.LIGHT_PURPLE));
                }
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("Error handling start book button", e);
            }
        });
    }

    public static void sendDetachRequest(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        // 不需要额外数据，只是一个解除吸附的信号

        ServerPlayNetworking.send(player, new BytePayload(BytePayload.id(JUMP_DETACH_REQUEST_ID), buf));
    }

    private static void onUpdatePlayerCustomConfig(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        boolean keepOriginalSkin = packetByteBuf.readBoolean();
        boolean enableFormColor = packetByteBuf.readBoolean();
        boolean enableFormRandomSound = packetByteBuf.readBoolean();
        minecraftServer.execute(() -> {
            try {
                PlayerSkinComponent component = RegPlayerSkinComponent.SKIN_SETTINGS.get(playerEntity);
                component.setKeepOriginalSkin(keepOriginalSkin);
                component.setEnableFormColor(enableFormColor);
                component.setEnableFormRandomSound(enableFormRandomSound);
                RegPlayerSkinComponent.SKIN_SETTINGS.sync(playerEntity);
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("Error while updating player custom config", e);
            }
        });
    }

    private static void onUpdatePlayerCustomColor(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int primaryColor = packetByteBuf.readInt();
        int accentColor1Color = packetByteBuf.readInt();
        int accentColor2Color = packetByteBuf.readInt();
        int eyeColorA = packetByteBuf.readInt();
        int eyeColorB = packetByteBuf.readInt();
        boolean primaryGreyReverse = packetByteBuf.readBoolean();
        boolean accent1GreyReverse = packetByteBuf.readBoolean();
        boolean accent2GreyReverse = packetByteBuf.readBoolean();
        minecraftServer.execute(() -> {
            try {
                PlayerSkinComponent component = RegPlayerSkinComponent.SKIN_SETTINGS.get(playerEntity);
                component.setFormColor(new FormTextureUtils.ColorSetting(primaryColor, accentColor1Color, accentColor2Color, eyeColorA, eyeColorB, primaryGreyReverse, accent1GreyReverse, accent2GreyReverse));
                RegPlayerSkinComponent.SKIN_SETTINGS.sync(playerEntity);
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("Error while updating player custom color", e);
            }
        });
    }

    private static void onUpdatePowerAnimationData(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        @Nullable Identifier animationId;
        if (packetByteBuf.readBoolean()) {
            animationId = packetByteBuf.readIdentifier();
        } else {
            animationId = null;
        }
        int animationCount = packetByteBuf.readInt();
        int animationLength = packetByteBuf.readInt();
        minecraftServer.execute(() -> {
            if (playerEntity instanceof IPlayerAnimController animPlayer) {
                if (animationId == null) {
                    animPlayer.shape_shifter_curse$stopAnimation();
                }
                else {
                    if (animationCount >= 0 && animationLength < 0) {  // >=0 / -1
                        animPlayer.shape_shifter_curse$playAnimationWithCount(animationId, animationCount);
                    } else if (animationCount < 0 && animationLength >= 0)  {  // -1 / >=0
                        animPlayer.shape_shifter_curse$playAnimationWithTime(animationId, animationLength);
                    } else if (animationCount < 0 && animationLength < 0) {  // -1 / -1
                        animPlayer.shape_shifter_curse$playAnimationLoop(animationId);
                    } else {
	                    ShapeShifterCurseFabric.LOGGER.error("Invalid animation data received from player: {}", playerEntity.getUuidAsString());
                    }
                }
            }
        });
    }

    private static void onRequestPowerAnimationData(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        UUID targetPlayerUuid = packetByteBuf.readUuid();
        PlayerEntity targetPlayer = minecraftServer.getPlayerManager().getPlayer(targetPlayerUuid);
        minecraftServer.execute(() -> {
            if (targetPlayer instanceof IPlayerAnimController animPlayer) {
                ModPacketsS2CServer.sendPowerAnimationDataToClient(playerEntity, targetPlayerUuid, animPlayer.shape_shifter_curse$getPowerAnimationID(), animPlayer.shape_shifter_curse$getPowerAnimationCount(), animPlayer.shape_shifter_curse$getPowerAnimationTime());
            }
        });
    }

    private static void receiveSetForm(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        UUID targetPlayerUuid = packetByteBuf.readUuid();
        PlayerEntity target = minecraftServer.getPlayerManager().getPlayer(targetPlayerUuid);
        if (target == null) {
            ShapeShifterCurseFabric.LOGGER.warn("[SetForm] Player {} not found", targetPlayerUuid);
        }
        Identifier formId = packetByteBuf.readIdentifier();
        PlayerFormBase form = RegPlayerForms.getPlayerForm(formId);
        // 网络包可以伪造 所以加个权限验证
        if (playerEntity.getCommandSource().hasPermissionLevel(2) || playerEntity.getAbilities().creativeMode) {
            minecraftServer.execute(() -> {
                if (target == null) {
                    ShapeShifterCurseFabric.LOGGER.warn("[SetForm] Player is null");
                    return;
                }
                TransformManager.handleDirectTransform(target, form, false);
            });
            return;
        }
    }

    private static void receiveSetPatronForm(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        if (!PatronUtils.EnablePatronFeature) {
	        String playerName = playerEntity.getDisplayName() != null ? playerEntity.getDisplayName().getString() : playerEntity.getName().getString();
	        ShapeShifterCurseFabric.LOGGER.error("Player {} tried to use patron form but patron feature is disabled", playerName);
            return;
        }
        Identifier formId = packetByteBuf.readIdentifier();
        PlayerFormBase form = RegPlayerForms.getPlayerForm(formId);

        if (minecraftServer.getCommandSource().hasPermissionLevel(2) || playerEntity.getAbilities().creativeMode) {
            // 权限等级2时跳过反作弊 毕竟可以用setForm了
            minecraftServer.execute(() -> {
                if (playerEntity == null) {
                    ShapeShifterCurseFabric.LOGGER.warn("[SetPatronForm] Player is null");
                    return;
                }
                TransformManager.handleDirectTransform(playerEntity, form, false);
            });
            return;
        }
        if (form instanceof PlayerFormDynamic pfd) {
            minecraftServer.execute(() -> {
                if (playerEntity == null) {
                    ShapeShifterCurseFabric.LOGGER.warn("[SetPatronForm] Player is null");
                    return;
                }
                if (pfd.IsPlayerCanUse(playerEntity)) {
                    TransformManager.handleDirectTransform(playerEntity, pfd, false);
                }
                else {
                    // 一般情况下，这里不会执行，因为客户端在发送请求前已经进行了检查 如果触发了这里，说明客户端和服务器之间的数据不同步(小概率 如果不同步早就掉线了) 或者是客户端作弊(大概率)
	                String playerName = playerEntity.getDisplayName() != null ? playerEntity.getDisplayName().getString() : playerEntity.getName().getString();
	                ShapeShifterCurseFabric.LOGGER.warn("Player {} tried to use form {} but they are not allowed", playerName, formId.toString());
                }
            });
        }
        else if (form != null){
            // 如果是已发布版本 100% 是客户端作弊 一般只会在测试时触发(因为测试版需要填充所有表单用来测试UI)
	        String playerName = playerEntity.getDisplayName() != null ? playerEntity.getDisplayName().getString() : playerEntity.getName().getString();
	        ShapeShifterCurseFabric.LOGGER.warn("Player {} tried to use form {} but it is not a dynamic form", playerName, formId.toString());
        }
        else {
            // 可能是不同步问题
	        String playerName = playerEntity.getDisplayName() != null ? playerEntity.getDisplayName().getString() : playerEntity.getName().getString();
	        ShapeShifterCurseFabric.LOGGER.warn("Player {} tried to use form {} but it does not exist", playerName, formId.toString());
        }
        return;
    }
}
