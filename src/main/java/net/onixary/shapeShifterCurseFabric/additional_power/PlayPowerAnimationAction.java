package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class PlayPowerAnimationAction {

    private static AnimUtils.AnimationSendSideType getAnimationSendSideType(SerializableData.Instance data) {
        if (data.getBoolean("can_on_client") && data.getBoolean("can_on_server")) {
            return AnimUtils.AnimationSendSideType.BOTH_SIDE;
        } else if (data.getBoolean("can_on_client")) {
            return AnimUtils.AnimationSendSideType.ONLY_CLIENT;
        } else if (data.getBoolean("can_on_server")) {
            return AnimUtils.AnimationSendSideType.ONLY_SERVER;
        } else {
            return AnimUtils.AnimationSendSideType.NONE;
        }
    }

    public static class PlayWithTimeAction extends EntityActionType {
        public static final TypedDataObjectFactory<PlayWithTimeAction> DATA_FACTORY = TypedDataObjectFactory.simple(
                new SerializableData()
                        .add("power_animation_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("animation_time", SerializableDataTypes.INT, 0)
                        .add("can_on_client", SerializableDataTypes.BOOLEAN, false)
                        .add("can_on_server", SerializableDataTypes.BOOLEAN, true),
                data -> new PlayWithTimeAction(data.getId("power_animation_id"), data.getInt("animation_time"), data),
                (action, serializableData) -> serializableData.instance()
        );

        private final Identifier powerAnimationId;
        private final int animationTime;
        private final SerializableData.Instance dataInstance;

        public PlayWithTimeAction(Identifier powerAnimationId, int animationTime, SerializableData.Instance dataInstance) {
            this.powerAnimationId = powerAnimationId;
            this.animationTime = animationTime;
            this.dataInstance = dataInstance;
        }

        @Override
        public void accept(EntityActionContext context) {
            if (animationTime <= 0 || powerAnimationId == null) return;
            if (context.entity() instanceof PlayerEntity player) {
                AnimUtils.playPowerAnimWithTime(player, powerAnimationId, animationTime, getAnimationSendSideType(dataInstance));
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("play_power_animation_with_time"), DATA_FACTORY);
        }
    }

    public static class PlayWithCountAction extends EntityActionType {
        public static final TypedDataObjectFactory<PlayWithCountAction> DATA_FACTORY = TypedDataObjectFactory.simple(
                new SerializableData()
                        .add("power_animation_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("animation_count", SerializableDataTypes.INT, 1)
                        .add("can_on_client", SerializableDataTypes.BOOLEAN, false)
                        .add("can_on_server", SerializableDataTypes.BOOLEAN, true),
                data -> new PlayWithCountAction(data.getId("power_animation_id"), data.getInt("animation_count"), data),
                (action, serializableData) -> serializableData.instance()
        );

        private final Identifier powerAnimationId;
        private final int animationCount;
        private final SerializableData.Instance dataInstance;

        public PlayWithCountAction(Identifier powerAnimationId, int animationCount, SerializableData.Instance dataInstance) {
            this.powerAnimationId = powerAnimationId;
            this.animationCount = animationCount;
            this.dataInstance = dataInstance;
        }

        @Override
        public void accept(EntityActionContext context) {
            if (animationCount <= 0 || powerAnimationId == null) return;
            if (context.entity() instanceof PlayerEntity player) {
                AnimUtils.playPowerAnimWithCount(player, powerAnimationId, animationCount, getAnimationSendSideType(dataInstance));
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("play_power_animation_with_count"), DATA_FACTORY);
        }
    }

    public static class PlayLoopAction extends EntityActionType {
        public static final TypedDataObjectFactory<PlayLoopAction> DATA_FACTORY = TypedDataObjectFactory.simple(
                new SerializableData()
                        .add("power_animation_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("can_on_client", SerializableDataTypes.BOOLEAN, false)
                        .add("can_on_server", SerializableDataTypes.BOOLEAN, true),
                data -> new PlayLoopAction(data.getId("power_animation_id"), data),
                (action, serializableData) -> serializableData.instance()
        );

        private final Identifier powerAnimationId;
        private final SerializableData.Instance dataInstance;

        public PlayLoopAction(Identifier powerAnimationId, SerializableData.Instance dataInstance) {
            this.powerAnimationId = powerAnimationId;
            this.dataInstance = dataInstance;
        }

        @Override
        public void accept(EntityActionContext context) {
            if (powerAnimationId == null) return;
            if (context.entity() instanceof PlayerEntity player) {
                AnimUtils.playPowerAnimLoop(player, powerAnimationId, getAnimationSendSideType(dataInstance));
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("play_power_animation_loop"), DATA_FACTORY);
        }
    }

    public static class StopAction extends EntityActionType {
        public static final TypedDataObjectFactory<StopAction> DATA_FACTORY = TypedDataObjectFactory.simple(
                new SerializableData()
                        .add("anim_id_list", SerializableDataTypes.IDENTIFIERS, null)
                        .add("can_on_client", SerializableDataTypes.BOOLEAN, false)
                        .add("can_on_server", SerializableDataTypes.BOOLEAN, true),
                data -> new StopAction(data.get("anim_id_list"), data),
                (action, serializableData) -> serializableData.instance()
        );

        private final List<Identifier> animIdList;
        private final SerializableData.Instance dataInstance;

        public StopAction(List<Identifier> animIdList, SerializableData.Instance dataInstance) {
            this.animIdList = animIdList;
            this.dataInstance = dataInstance;
        }

        @Override
        public void accept(EntityActionContext context) {
            if (!(context.entity() instanceof PlayerEntity player)) return;
            AnimUtils.AnimationSendSideType sideType = getAnimationSendSideType(dataInstance);
            if (animIdList == null || animIdList.isEmpty()) {
                AnimUtils.stopPowerAnim(player, sideType);
            } else {
                AnimUtils.stopPowerAnimWithIDs(player, sideType, animIdList);
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("stop_power_animation"), DATA_FACTORY);
        }
    }

    public static void register(Consumer<ActionConfiguration<EntityActionType>> actionReg,
                                 Consumer<?> biActionReg) {
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("play_power_animation_with_time"),
                PlayWithTimeAction.DATA_FACTORY
        ));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("play_power_animation_with_count"),
                PlayWithCountAction.DATA_FACTORY
        ));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("play_power_animation_loop"),
                PlayLoopAction.DATA_FACTORY
        ));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("stop_power_animation"),
                StopAction.DATA_FACTORY
        ));
    }
}