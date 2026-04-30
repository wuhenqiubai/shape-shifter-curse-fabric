package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.BiEntityAction;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.BiEntityActionContext;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import net.onixary.shapeShifterCurseFabric.minion.MinionRegister;
import net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SummonMinionWolfNearbyAction {

    public static final TypedDataObjectFactory<SummonBIAction> BI_DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("minion_level", SerializableDataTypes.INT, 1)
                    .add("count", SerializableDataTypes.INT, 1)
                    .add("max_minion_count", SerializableDataTypes.INT, Integer.MAX_VALUE)
                    .add("cooldown", SerializableDataTypes.INT, 0)
                    .add("owner_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add("target_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add("reverse", SerializableDataTypes.BOOLEAN, false),
            data -> new SummonBIAction(
                    data.getInt("minion_level"),
                    data.getInt("count"),
                    data.getInt("max_minion_count"),
                    data.getInt("cooldown"),
                    data.get("owner_action"),
                    data.get("target_action"),
                    data.getBoolean("reverse")
            ),
            (action, serializableData) -> serializableData.instance()
    );

    public static class SummonBIAction extends BiEntityActionType {
        private final int minionLevel, count, maxMinionCount, cooldown;
        private final Optional<EntityAction> ownerAction, targetAction;
        private final boolean reverse;

        public SummonBIAction(int minionLevel, int count, int maxMinionCount, int cooldown,
                              Optional<EntityAction> ownerAction, Optional<EntityAction> targetAction, boolean reverse) {
            this.minionLevel = minionLevel;
            this.count = count;
            this.maxMinionCount = maxMinionCount;
            this.cooldown = cooldown;
            this.ownerAction = ownerAction;
            this.targetAction = targetAction;
            this.reverse = reverse;
        }

        @Override
        public void accept(BiEntityActionContext context) {
            Entity owner = reverse ? context.actor2() : context.actor1();
            Entity spawnNearbyTarget = reverse ? context.actor1() : context.actor2();
            spawnMinions(owner, spawnNearbyTarget);
        }

        private void spawnMinions(Entity owner, Entity spawnNearbyTarget) {
            if (!(owner instanceof ServerPlayerEntity player)) return;
            boolean summonSuccess = false;
            for (int i = 0; i < count; i++) {
                if (player instanceof IPlayerEntityMinion playerMinion) {
                    if (playerMinion.shape_shifter_curse$getMinionsCount(AnubisWolfMinionEntity.MinionID) >= maxMinionCount) return;
                    if (MinionRegister.IsInCoolDown(AnubisWolfMinionEntity.MinionID, player, cooldown)) return;
                } else {
                    ShapeShifterCurseFabric.LOGGER.warn("Can't spawn minion, player is not IPlayerEntityMinion");
                    return;
                }
                BlockPos targetPos = MinionRegister.getNearbyEmptySpace(spawnNearbyTarget.getWorld(), player.getRandom(), spawnNearbyTarget.getBlockPos(), 3, 1, 1, 4);
                if (targetPos == null) targetPos = spawnNearbyTarget.getBlockPos();
                if (spawnNearbyTarget.getWorld() instanceof ServerWorld world) {
                    AnubisWolfMinionEntity minion = MinionRegister.SpawnMinion(MinionRegister.ANUBIS_WOLF_MINION, world, targetPos, player);
                    if (minion != null) {
                        minion.setMinionLevel(minionLevel);
                        summonSuccess = true;
                    } else {
                        ShapeShifterCurseFabric.LOGGER.warn("Can't spawn minion, wolfMinion is null");
                    }
                } else {
                    ShapeShifterCurseFabric.LOGGER.warn("Can't spawn minion, world is not ServerWorld");
                }
            }
            if (summonSuccess) {
                MinionRegister.SetCoolDown(AnubisWolfMinionEntity.MinionID, player);
                ownerAction.ifPresent(a -> a.accept(owner));
                targetAction.ifPresent(a -> a.accept(spawnNearbyTarget));
                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WOLF_GROWL, player.getSoundCategory(), 1.0f, 1.5f);
                    serverWorld.spawnParticles(player, ParticleTypes.SOUL_FIRE_FLAME, true, player.getBlockPos().getX() + 0.5f, player.getBlockPos().getY() + 0.5f, player.getBlockPos().getZ() + 0.5f, 8, 0, 0, 0, 0);
                }
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return AdditionalEntityActions.SUMMON_MINION_WOLF_BI;
        }
    }

    public static ActionConfiguration<SummonBIAction> createBIConfig(Identifier id) {
        return ActionConfiguration.of(id, BI_DATA_FACTORY);
    }

    public static final TypedDataObjectFactory<SummonAction> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("minion_level", SerializableDataTypes.INT, 1)
                    .add("count", SerializableDataTypes.INT, 1)
                    .add("max_minion_count", SerializableDataTypes.INT, Integer.MAX_VALUE)
                    .add("cooldown", SerializableDataTypes.INT, 0)
                    .add("owner_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add("target_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add("reverse", SerializableDataTypes.BOOLEAN, false),
            data -> new SummonAction(
                    data.getInt("minion_level"),
                    data.getInt("count"),
                    data.getInt("max_minion_count"),
                    data.getInt("cooldown"),
                    data.get("owner_action"),
                    data.get("target_action"),
                    data.getBoolean("reverse")
            ),
            (action, serializableData) -> serializableData.instance()
    );

    public static class SummonAction extends EntityActionType {
        private final int minionLevel, count, maxMinionCount, cooldown;
        private final Optional<EntityAction> ownerAction, targetAction;
        private final boolean reverse;

        public SummonAction(int minionLevel, int count, int maxMinionCount, int cooldown,
                            Optional<EntityAction> ownerAction, Optional<EntityAction> targetAction, boolean reverse) {
            this.minionLevel = minionLevel;
            this.count = count;
            this.maxMinionCount = maxMinionCount;
            this.cooldown = cooldown;
            this.ownerAction = ownerAction;
            this.targetAction = targetAction;
            this.reverse = reverse;
        }

        @Override
        public void accept(EntityActionContext context) {
            new SummonBIAction(minionLevel, count, maxMinionCount, cooldown, ownerAction, targetAction, reverse)
                    .spawnMinions(context.entity(), context.entity());
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return AdditionalEntityActions.SUMMON_MINION_WOLF;
        }
    }

    public static ActionConfiguration<SummonAction> createConfig(Identifier id) {
        return ActionConfiguration.of(id, DATA_FACTORY);
    }

    public static void register(java.util.function.Consumer<ActionConfiguration<SummonAction>> actionReg,
                                 java.util.function.Consumer<ActionConfiguration<SummonBIAction>> biActionReg) {
        actionReg.accept(createConfig(ShapeShifterCurseFabric.identifier("summon_anubis_wolf_minion")));
        biActionReg.accept(createBIConfig(ShapeShifterCurseFabric.identifier("bi_summon_anubis_wolf_minion")));
    }
}