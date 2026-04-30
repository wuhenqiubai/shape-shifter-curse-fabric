package net.onixary.shapeShifterCurseFabric.additional_power;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.condition.ItemCondition;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryUtils;
import net.onixary.shapeShifterCurseFabric.items.accessory.CurioUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryUtils.calcAutoMod;

public class TrinketsConditionAction {

    private static boolean isEquipped(LivingEntity entity, String accessoryMod, Identifier trinketID) {
        if (trinketID == null) return false;
        Optional<Item> trinketItem = Registries.ITEM.getOrEmpty(trinketID);
        if (trinketItem.isEmpty()) return false;
        return switch (calcAutoMod(accessoryMod)) {
            case "trinkets" -> {
                if (!AccessoryUtils.LOADED_Trinkets) yield false;
                yield TrinketsApi.getTrinketComponent(entity)
                        .map(c -> c.isEquipped(trinketItem.get())).orElse(false);
            }
            case "curios" -> AccessoryUtils.LOADED_Curios && CurioUtils.isEquipped(entity, trinketItem.get());
            case "all" -> {
                boolean found = false;
                if (AccessoryUtils.LOADED_Trinkets)
                    found = TrinketsApi.getTrinketComponent(entity).map(c -> c.isEquipped(trinketItem.get())).orElse(false);
                if (!found && AccessoryUtils.LOADED_Curios)
                    found = CurioUtils.isEquipped(entity, trinketItem.get());
                yield found;
            }
            default -> false;
        };
    }

    private static boolean checkEquipped(LivingEntity livingEntity, String accessoryMod,
                                          String group, String slot, int slotIndex,
                                          Optional<ItemCondition> condition, boolean rDefault) {
        return switch (calcAutoMod(accessoryMod)) {
            case "trinkets" -> {
                if (!AccessoryUtils.LOADED_Trinkets) yield rDefault;
                Optional<TrinketComponent> comp = TrinketsApi.getTrinketComponent(livingEntity);
                if (comp.isEmpty()) yield rDefault;
                Map<String, TrinketInventory> groupInv = comp.get().getInventory().get(group);
                if (groupInv == null) yield rDefault;
                TrinketInventory inv = groupInv.get(slot);
                if (inv == null) yield rDefault;
                yield condition.map(c -> c.test(inv.getStack(slotIndex))).orElse(rDefault);
            }
            case "curios" -> {
                if (!AccessoryUtils.LOADED_Curios) yield rDefault;
                List<ItemStack> stacks = CurioUtils.getEntitySlot(livingEntity, slot);
                if (stacks == null || slotIndex >= stacks.size()) yield rDefault;
                yield condition.map(c -> c.test(stacks.get(slotIndex))).orElse(rDefault);
            }
            default -> rDefault;
        };
    }

    private static void invokeEquipped(LivingEntity entity, String accessoryMod, String group, String slot, int slotIndex,
                                        Optional<Consumer<ItemStack>> action) {
        if (action.isEmpty()) return;
        switch (calcAutoMod(accessoryMod)) {
            case "trinkets" -> {
                if (!AccessoryUtils.LOADED_Trinkets) return;
                TrinketsApi.getTrinketComponent(entity).ifPresent(comp -> {
                    Map<String, TrinketInventory> groupInv = comp.getInventory().get(group);
                    if (groupInv == null) return;
                    TrinketInventory inv = groupInv.get(slot);
                    if (inv == null) return;
                    action.get().accept(inv.getStack(slotIndex));
                });
            }
            case "curios" -> {
                if (!AccessoryUtils.LOADED_Curios) return;
                List<ItemStack> stacks = CurioUtils.getEntitySlot(entity, slot);
                if (stacks != null && slotIndex < stacks.size())
                    action.get().accept(stacks.get(slotIndex));
            }
        }
    }

    private static void dropEquipped(LivingEntity entity, String accessoryMod, String group, String slot,
                                      int slotIndex, boolean remove) {
        switch (calcAutoMod(accessoryMod)) {
            case "trinkets" -> {
                if (!AccessoryUtils.LOADED_Trinkets) return;
                Optional<TrinketComponent> comp = TrinketsApi.getTrinketComponent(entity);
                if (comp.isEmpty()) return;
                Map<String, TrinketInventory> groupInv = comp.get().getInventory().get(group);
                if (groupInv == null) return;
                TrinketInventory inv = groupInv.get(slot);
                if (inv == null) return;
                World world = entity.getWorld();
                if (slotIndex >= 0) {
                    dropOrRemove(world, entity, inv, slotIndex, remove);
                } else {
                    for (int i = 0; i < inv.size(); i++)
                        dropOrRemove(world, entity, inv, i, remove);
                }
            }
            case "curios" -> {
                if (!AccessoryUtils.LOADED_Curios) return;
                List<ItemStack> stacks = CurioUtils.getEntitySlot(entity, slot);
                if (stacks == null) return;
                World world = entity.getWorld();
                if (slotIndex >= 0 && slotIndex < stacks.size()) {
                    dropOrRemoveSL(world, entity, stacks, slotIndex, remove);
                } else if (slotIndex < 0) {
                    for (int i = 0; i < stacks.size(); i++)
                        dropOrRemoveSL(world, entity, stacks, i, remove);
                }
            }
        }
    }

    private static void dropOrRemove(World world, LivingEntity entity, TrinketInventory inv, int idx, boolean remove) {
        if (remove) {
            inv.setStack(idx, ItemStack.EMPTY);
        } else if (!inv.getStack(idx).isEmpty()) {
            world.spawnEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), inv.getStack(idx)));
            inv.setStack(idx, ItemStack.EMPTY);
        }
    }

    private static void dropOrRemoveSL(World world, LivingEntity entity, List<ItemStack> stacks, int idx, boolean remove) {
        if (remove) {
            stacks.set(idx, ItemStack.EMPTY);
        } else if (!stacks.get(idx).isEmpty()) {
            world.spawnEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), stacks.get(idx)));
            stacks.set(idx, ItemStack.EMPTY);
        }
    }

    public static class EquipAccessoryCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<EquipAccessoryCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("accessory_mod", SerializableDataTypes.STRING, "auto")
                                .add("accessory", SerializableDataTypes.IDENTIFIER, null),
                        data -> new EquipAccessoryCondition(data.getString("accessory_mod"), data.getId("accessory")),
                        (c, sd) -> sd.instance()
                );

        private final String accessoryMod;
        private final Identifier accessory;

        public EquipAccessoryCondition(String accessoryMod, Identifier accessory) {
            this.accessoryMod = accessoryMod;
            this.accessory = accessory;
        }

        @Override
        public boolean test(EntityConditionContext ctx) {
            return ctx.entity() instanceof LivingEntity le && isEquipped(le, accessoryMod, accessory);
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return AdditionalEntityConditions.EQUIP_ACCESSORY;
        }
    }

    public static class CheckAccessoryCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<CheckAccessoryCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("accessory_mod", SerializableDataTypes.STRING, "auto")
                                .add("group", SerializableDataTypes.STRING, "")
                                .add("slot", SerializableDataTypes.STRING, "")
                                .add("slot_index", SerializableDataTypes.INT, 0)
                                .add("condition", ItemCondition.DATA_TYPE.optional(), Optional.empty()),
                        data -> new CheckAccessoryCondition(
                                data.getString("accessory_mod"), data.getString("group"),
                                data.getString("slot"), data.getInt("slot_index"), data.get("condition")),
                        (c, sd) -> sd.instance()
                );

        private final String accessoryMod, group, slot;
        private final int slotIndex;
        private final Optional<ItemCondition> condition;

        public CheckAccessoryCondition(String accessoryMod, String group, String slot,
                                        int slotIndex, Optional<ItemCondition> condition) {
            this.accessoryMod = accessoryMod;
            this.group = group;
            this.slot = slot;
            this.slotIndex = slotIndex;
            this.condition = condition;
        }

        @Override
        public boolean test(EntityConditionContext ctx) {
            return ctx.entity() instanceof LivingEntity le
                    && checkEquipped(le, accessoryMod, group, slot, slotIndex, condition, false);
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return AdditionalEntityConditions.CHECK_ACCESSORY;
        }
    }

    public static class DropAccessoryAction extends EntityActionType {
        public static final TypedDataObjectFactory<DropAccessoryAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("accessory_mod", SerializableDataTypes.STRING, "auto")
                                .add("group", SerializableDataTypes.STRING, "")
                                .add("slot", SerializableDataTypes.STRING, "")
                                .add("slot_index", SerializableDataTypes.INT, -1)
                                .add("remove", SerializableDataTypes.BOOLEAN, false),
                        data -> new DropAccessoryAction(
                                data.getString("accessory_mod"), data.getString("group"),
                                data.getString("slot"), data.getInt("slot_index"), data.getBoolean("remove")),
                        (a, sd) -> sd.instance()
                );

        private final String accessoryMod, group, slot;
        private final int slotIndex;
        private final boolean remove;

        public DropAccessoryAction(String accessoryMod, String group, String slot, int slotIndex, boolean remove) {
            this.accessoryMod = accessoryMod;
            this.group = group;
            this.slot = slot;
            this.slotIndex = slotIndex;
            this.remove = remove;
        }

        @Override
        public void accept(EntityActionContext ctx) {
            if (ctx.entity() instanceof LivingEntity le)
                dropEquipped(le, accessoryMod, group, slot, slotIndex, remove);
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return AdditionalEntityActions.DROP_ACCESSORY;
        }
    }

    public static class InvokeAccessoryAction extends EntityActionType {
        public static final TypedDataObjectFactory<InvokeAccessoryAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("accessory_mod", SerializableDataTypes.STRING, "auto")
                                .add("group", SerializableDataTypes.STRING, "")
                                .add("slot", SerializableDataTypes.STRING, "")
                                .add("slot_index", SerializableDataTypes.INT, 0)
                                .add("item_action", ItemCondition.DATA_TYPE.optional(), Optional.empty()),
                        data -> new InvokeAccessoryAction(
                                data.getString("accessory_mod"), data.getString("group"),
                                data.getString("slot"), data.getInt("slot_index"), data.get("item_action")),
                        (a, sd) -> sd.instance()
                );

        private final String accessoryMod, group, slot;
        private final int slotIndex;
        private final Optional<ItemCondition> itemAction;

        public InvokeAccessoryAction(String accessoryMod, String group, String slot,
                                      int slotIndex, Optional<ItemCondition> itemAction) {
            this.accessoryMod = accessoryMod;
            this.group = group;
            this.slot = slot;
            this.slotIndex = slotIndex;
            this.itemAction = itemAction;
        }

        @Override
        public void accept(EntityActionContext ctx) {
            if (!(ctx.entity() instanceof LivingEntity le) || itemAction.isEmpty()) return;
            Consumer<ItemStack> action = itemAction.get()::test;
            invokeEquipped(le, accessoryMod, group, slot, slotIndex, Optional.of(action));
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return AdditionalEntityActions.INVOKE_ACCESSORY;
        }
    }

    public static void registerCondition(Consumer<ConditionConfiguration<EquipAccessoryCondition>> reg) {
        reg.accept(ConditionConfiguration.of(
                ShapeShifterCurseFabric.identifier("equip_accessory"), EquipAccessoryCondition.DATA_FACTORY));
        reg.accept(ConditionConfiguration.of(
                ShapeShifterCurseFabric.identifier("check_accessory"), CheckAccessoryCondition.DATA_FACTORY));
    }

    public static void registerAction(Consumer<ActionConfiguration<DropAccessoryAction>> actionReg,
                                       Consumer<?> biActionReg) {
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("drop_accessory"), DropAccessoryAction.DATA_FACTORY));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("invoke_accessory"), InvokeAccessoryAction.DATA_FACTORY));
    }
}