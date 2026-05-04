package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemCooldownCA {

    public static class IsItemInCooldownCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<IsItemInCooldownCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData().add("item", SerializableDataTypes.ITEM, null),
                        data -> new IsItemInCooldownCondition(data.get("item")),
                        (action, sd) -> sd.instance()
                );

        private final Item item;

        public IsItemInCooldownCondition(Item item) { this.item = item; }

        @Override
        public boolean test(EntityConditionContext ctx) {
            if (item == null) return false;
            return ctx.entity() instanceof PlayerEntity p && p.getItemCooldownManager().isCoolingDown(item);
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("is_item_in_cooldown"), DATA_FACTORY);
        }
    }

    public static class SetItemCooldownAction extends EntityActionType {
        public static final TypedDataObjectFactory<SetItemCooldownAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("item", SerializableDataTypes.ITEM, null)
                                .add("cooldown", SerializableDataTypes.INT, 0),
                        data -> new SetItemCooldownAction(data.get("item"), data.getInt("cooldown")),
                        (action, sd) -> sd.instance()
                );

        private final Item item;
        private final int cooldown;

        public SetItemCooldownAction(Item item, int cooldown) { this.item = item; this.cooldown = cooldown; }

        @Override
        public void accept(EntityActionContext ctx) {
            if (item != null && ctx.entity() instanceof PlayerEntity p)
                p.getItemCooldownManager().set(item, cooldown);
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("set_item_cooldown"), DATA_FACTORY);
        }
    }

    public static void registerCondition(Consumer<ConditionConfiguration<IsItemInCooldownCondition>> reg) {
        reg.accept(ConditionConfiguration.of(
                ShapeShifterCurseFabric.identifier("is_item_in_cooldown"), IsItemInCooldownCondition.DATA_FACTORY));
    }

    public static void registerAction(Consumer<ActionConfiguration<SetItemCooldownAction>> actionReg,
                                       Consumer<ActionConfiguration<? extends BiEntityActionType>> biActionReg) {
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("set_item_cooldown"), SetItemCooldownAction.DATA_FACTORY));
    }
}