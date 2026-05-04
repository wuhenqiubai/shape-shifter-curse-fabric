package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.CooldownPowerType;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class VirtualTotemPower extends CooldownPowerType {
    public static final HashMap<Identifier, BiConsumer<PlayerEntity, ItemStack>> virtualTotemTypeMap = new HashMap<>();

    static {
        virtualTotemTypeMap.put(ShapeShifterCurseFabric.identifier("default"), (PlayerEntity playerEntity, ItemStack totemStack) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (totemStack == null) {
                totemStack = new ItemStack(Items.TOTEM_OF_UNDYING, 1);
            }
            if (client.world != null) {
                client.particleManager.addEmitter(playerEntity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                client.world.playSound(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ITEM_TOTEM_USE, playerEntity.getSoundCategory(), 1.0f, 1.0f, false);
                if (playerEntity != client.player) return;
                client.gameRenderer.showFloatingItem(totemStack);
            }
        });
        virtualTotemTypeMap.put(ShapeShifterCurseFabric.identifier("form_anubis_wolf_3_undying"), (PlayerEntity playerEntity, ItemStack totemStack) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null) {
                client.particleManager.addEmitter(playerEntity, ParticleTypes.SMOKE, 30);
                client.particleManager.addEmitter(playerEntity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                client.world.playSound(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_WITHER_DEATH, playerEntity.getSoundCategory(), 0.75f, 0.8f, false);
            }
        });
    }

    public Identifier virtualTotemType;
    public ItemStack totemStack;
    private final List<EntityAction> entityAction;
    private final int totemHealth;
    private final List<StatusEffectInstance> totemStatusEffects;

    public static final TypedDataObjectFactory<VirtualTotemPower> DATA_FACTORY =
            TypedDataObjectFactory.simple(
                    new SerializableData()
                            .add("virtual_totem_type", SerializableDataTypes.IDENTIFIER, ShapeShifterCurseFabric.identifier("default"))
                            .add("totem_stack", SerializableDataTypes.ITEM_STACK, new ItemStack(Items.TOTEM_OF_UNDYING, 1))
                            .add("entity_actions", EntityAction.DATA_TYPE.list().optional(), Optional.empty())
                            .add("totem_health", SerializableDataTypes.INT, 1)
                            .add("totem_status_effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null)
                            .add("cooldown", SerializableDataTypes.INT, 1200)
                            .add("hud_render", HudRender.DATA_TYPE, HudRender.DONT_RENDER),
                    data -> new VirtualTotemPower(data),
                    (power, sd) -> sd.instance()
                            .set("virtual_totem_type", power.virtualTotemType)
                            .set("totem_stack", power.totemStack)
                            .set("entity_actions", power.entityAction)
                            .set("totem_health", power.totemHealth)
                            .set("totem_status_effects", power.totemStatusEffects)
                            .set("cooldown", power.getCooldown())
                            .set("hud_render", power.getRenderSettings())
            );

    public VirtualTotemPower(SerializableData.Instance data) {
        super(data.getInt("cooldown"), data.get("hud_render"));
        this.virtualTotemType = data.get("virtual_totem_type");
        this.totemStack = data.get("totem_stack");
        this.entityAction = data.get("entity_actions");
        this.totemHealth = data.getInt("totem_health");
        this.totemStatusEffects = data.get("totem_status_effects");
    }

    public NbtElement toTag() {
        return super.toTag();
    }

    public void fromTag(NbtElement tag) {
        super.fromTag(tag);
    }

    public void use() {
        LivingEntity entity = getHolder();
        if (entity == null) {
            ShapeShifterCurseFabric.LOGGER.error("VirtualTotemPower: entity is null");
            return;
        }
        entity.setHealth(this.totemHealth);
        if (this.totemStatusEffects != null) {
            for (StatusEffectInstance statusEffectInstance : this.totemStatusEffects) {
                entity.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
            }
        }
        if (this.entityAction != null) {
            for (EntityAction action : this.entityAction) {
                action.accept(new EntityActionContext(entity));
            }
        }
        if (!entity.getWorld().isClient && entity instanceof ServerPlayerEntity serverPlayerEntity) {
            ModPacketsS2CServer.sendActiveVirtualTotem(serverPlayerEntity, this);
        }
        super.use();
    }

    public @Nullable PacketByteBuf create_packet_byte_buf() {
        LivingEntity entity = getHolder();
        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            PacketByteBuf packetByteBuf = PacketByteBufs.create();
            packetByteBuf.writeUuid(serverPlayerEntity.getUuid());
            packetByteBuf.writeIdentifier(this.virtualTotemType);
            ItemStack.PACKET_CODEC.encode(packetByteBuf, this.totemStack);
            return packetByteBuf;
        }
        return null;
    }

    public static void process_virtual_totem_type(@NotNull PlayerEntity entity, Identifier virtualTotemType, @Nullable ItemStack totemStack) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (virtualTotemTypeMap.containsKey(virtualTotemType)) {
            virtualTotemTypeMap.get(virtualTotemType).accept(entity, totemStack);
        } else {
            ShapeShifterCurseFabric.LOGGER.error("VirtualTotemPower: unknown virtualTotemType: {}", virtualTotemType);
        }
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("virtual_totem"));
    }

    public static PowerConfiguration<VirtualTotemPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}