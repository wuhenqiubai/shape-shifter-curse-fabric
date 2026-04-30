package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableData.Operations;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ManaUtilsApoli {

    public static class SetManaAction extends EntityActionType {
        public static final TypedDataObjectFactory<SetManaAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData().add("mana", SerializableDataTypes.DOUBLE, 0.0d),
                        data -> new SetManaAction(data.getDouble("mana")),
                        (a, sd) -> sd.instance()
                );
        private final double mana;
        public SetManaAction(double mana) { this.mana = mana; }

        @Override
        public void accept(EntityActionContext ctx) {
            if (ctx.entity() instanceof ServerPlayerEntity p) ManaUtils.setPlayerMana(p, mana);
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("set_mana"), DATA_FACTORY);
        }
    }

    public static class GainManaAction extends EntityActionType {
        public static final TypedDataObjectFactory<GainManaAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData().add("mana", SerializableDataTypes.DOUBLE, 0.0d),
                        data -> new GainManaAction(data.getDouble("mana")),
                        (a, sd) -> sd.instance()
                );
        private final double mana;
        public GainManaAction(double mana) { this.mana = mana; }

        @Override
        public void accept(EntityActionContext ctx) {
            if (ctx.entity() instanceof ServerPlayerEntity p) ManaUtils.gainPlayerMana(p, mana);
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("gain_mana"), DATA_FACTORY);
        }
    }

    public static class ConsumeManaAction extends EntityActionType {
        public static final TypedDataObjectFactory<ConsumeManaAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData().add("mana", SerializableDataTypes.DOUBLE, 0.0d),
                        data -> new ConsumeManaAction(data.getDouble("mana")),
                        (a, sd) -> sd.instance()
                );
        private final double mana;
        public ConsumeManaAction(double mana) { this.mana = mana; }

        @Override
        public void accept(EntityActionContext ctx) {
            if (ctx.entity() instanceof ServerPlayerEntity p) ManaUtils.consumePlayerMana(p, mana);
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("consume_mana"), DATA_FACTORY);
        }
    }

    public static class GainManaWithTimeAction extends EntityActionType {
        public static final TypedDataObjectFactory<GainManaWithTimeAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("mana", SerializableDataTypes.DOUBLE, 0.0d)
                                .add("time", SerializableDataTypes.INT, 0),
                        data -> new GainManaWithTimeAction(data.getDouble("mana"), data.getInt("time")),
                        (a, sd) -> sd.instance()
                );
        private final double mana;
        private final int time;
        public GainManaWithTimeAction(double mana, int time) { this.mana = mana; this.time = time; }

        @Override
        public void accept(EntityActionContext ctx) {
            if (ctx.entity() instanceof ServerPlayerEntity p) ManaUtils.gainPlayerManaWithTime(p, mana, time);
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("gain_mana_with_time"), DATA_FACTORY);
        }
    }

    public static class HasManaCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<HasManaCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData().add("mana", SerializableDataTypes.DOUBLE, 0.0d),
                        data -> new HasManaCondition(data.getDouble("mana")),
                        (c, sd) -> sd.instance()
                );
        private final double mana;
        public HasManaCondition(double mana) { this.mana = mana; }

        @Override
        public boolean test(EntityConditionContext ctx) {
            return ctx.entity() instanceof PlayerEntity p && ManaUtils.isPlayerManaAbove(p, mana);
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("has_mana"), DATA_FACTORY);
        }
    }

    public static class HasManaPercentCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<HasManaPercentCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData().add("mana_percent", SerializableDataTypes.DOUBLE, 0.0d),
                        data -> new HasManaPercentCondition(data.getDouble("mana_percent")),
                        (c, sd) -> sd.instance()
                );
        private final double manaPercent;
        public HasManaPercentCondition(double manaPercent) { this.manaPercent = manaPercent; }

        @Override
        public boolean test(EntityConditionContext ctx) {
            return ctx.entity() instanceof PlayerEntity p
                    && ManaUtils.getPlayerManaPercent(p, 0.0d) >= manaPercent;
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("has_mana_percent"), DATA_FACTORY);
        }
    }

    public static class ManaCompareCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<ManaCompareCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("comparison", SerializableDataTypes.COMPARISON, null)
                                .add("compare_to", SerializableDataTypes.DOUBLE, 0.0d),
                        data -> new ManaCompareCondition(data.get("comparison"), data.getDouble("compare_to")),
                        (c, sd) -> sd.instance()
                );
        private final Comparison comparison;
        private final double compareTo;
        public ManaCompareCondition(Comparison comparison, double compareTo) {
            this.comparison = comparison; this.compareTo = compareTo;
        }

        @Override
        public boolean test(EntityConditionContext ctx) {
            return comparison != null && ctx.entity() instanceof PlayerEntity p
                    && comparison.compare(ManaUtils.getPlayerMana(p), compareTo);
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("mana_compare"), DATA_FACTORY);
        }
    }

    public static class ManaPercentCompareCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<ManaPercentCompareCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("comparison", SerializableDataTypes.COMPARISON, null)
                                .add("compare_to", SerializableDataTypes.DOUBLE, 0.0d),
                        data -> new ManaPercentCompareCondition(data.get("comparison"), data.getDouble("compare_to")),
                        (c, sd) -> sd.instance()
                );
        private final Comparison comparison;
        private final double compareTo;
        public ManaPercentCompareCondition(Comparison comparison, double compareTo) {
            this.comparison = comparison; this.compareTo = compareTo;
        }

        @Override
        public boolean test(EntityConditionContext ctx) {
            return comparison != null && ctx.entity() instanceof PlayerEntity p
                    && comparison.compare(ManaUtils.getPlayerManaPercent(p, 0.0d), compareTo);
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("mana_percent_compare"), DATA_FACTORY);
        }
    }

    public static void registerAction(Consumer<ActionConfiguration<EntityActionType>> actionReg,
                                       Consumer<?> biActionReg) {
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("set_mana"), SetManaAction.DATA_FACTORY));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("gain_mana"), GainManaAction.DATA_FACTORY));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("consume_mana"), ConsumeManaAction.DATA_FACTORY));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("gain_mana_with_time"), GainManaWithTimeAction.DATA_FACTORY));
    }

    public static void registerCondition(Consumer<ConditionConfiguration<EntityConditionType>> reg) {
        reg.accept(ConditionConfiguration.of(
                ShapeShifterCurseFabric.identifier("has_mana"), HasManaCondition.DATA_FACTORY));
        reg.accept(ConditionConfiguration.of(
                ShapeShifterCurseFabric.identifier("has_mana_percent"), HasManaPercentCondition.DATA_FACTORY));
        reg.accept(ConditionConfiguration.of(
                ShapeShifterCurseFabric.identifier("mana_compare"), ManaCompareCondition.DATA_FACTORY));
        reg.accept(ConditionConfiguration.of(
                ShapeShifterCurseFabric.identifier("mana_percent_compare"), ManaPercentCompareCondition.DATA_FACTORY));
    }
}