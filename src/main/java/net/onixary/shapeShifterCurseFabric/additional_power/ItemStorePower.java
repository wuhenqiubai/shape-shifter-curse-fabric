package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.condition.ItemCondition;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.render.tech.ItemStorePowerRender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class ItemStorePower extends PowerType implements ItemStorePowerRender.itemStorePowerRenderInterface {
    public ItemStack storedItem = ItemStack.EMPTY;
    public final @Nullable Identifier powerID;
    public int bobbingAnimationTime = 0;
    public final int Slot;
    public final int VanillaSlotStart = 2800;

    public static final TypedDataObjectFactory<ItemStorePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("id", SerializableDataTypes.IDENTIFIER, null)
                            .add("slot", SerializableDataTypes.INT, 0),
                    (data, condition) -> new ItemStorePower(condition, data.get("id"), data.getInt("slot")),
                    (power, sd) -> sd.instance()
            );

    public ItemStorePower(Optional<EntityCondition> condition, @Nullable Identifier powerID, int Slot) {
        super(condition);
        this.powerID = powerID;
        this.Slot = Slot;
        this.setTicking();
    }

    public void clientTick() {
        if (this.bobbingAnimationTime > 0) {
            this.bobbingAnimationTime -= 1;
        }
    }

    @Override
    public void serverTick() {
        LivingEntity entity = getHolder();
        if (this.bobbingAnimationTime > 0) {
            this.bobbingAnimationTime -= 1;
        }
        this.storedItem.inventoryTick(entity.getWorld(), entity, VanillaSlotStart + this.Slot, false);
    }

    public void SetItem(ItemStack stack) {
        LivingEntity entity = getHolder();
        if (entity.getWorld().isClient) {
            return;
        }
        this.storedItem = stack.copy();
        this.bobbingAnimationTime = 5;
        PowerHolderComponent.sync(entity);
    }

    public void GainItem(ItemStack stack) {
        LivingEntity entity = getHolder();
        if (entity.getWorld().isClient) {
            return;
        }
        if (!this.storedItem.isEmpty()) {
            this.DropItem();
        }
        this.SetItem(stack);
    }

    public void DropItem() {
        LivingEntity entity = getHolder();
        if (entity.getWorld().isClient) {
            return;
        }
        if (!storedItem.isEmpty()) {
            entity.getWorld().spawnEntity(
                    new ItemEntity(
                            entity.getWorld(),
                            entity.getX(),
                            entity.getY(),
                            entity.getZ(),
                            this.storedItem
                    )
            );
            this.SetItem(ItemStack.EMPTY);
        }
    }

    public void SwapItem(EquipmentSlot slot) {
        LivingEntity entity = getHolder();
        if (entity.getWorld().isClient) {
            return;
        }
        ItemStack item = entity.getEquippedStack(slot);
        ItemStack stored = this.storedItem;
        this.SetItem(item);
        entity.equipStack(slot, stored);
    }

    public void InvokeItemAction(Consumer<ItemStack> action) {
        LivingEntity entity = getHolder();
        if (entity.getWorld().isClient) {
            return;
        }
        if (action != null) {
            action.accept(this.storedItem);
        }
        PowerHolderComponent.sync(entity);
    }

    @Override
    public void onLost() {
        super.onLost();
        this.DropItem();
    }


    @Override
    public NbtElement toTag() {
        NbtCompound tag = new NbtCompound();
        NbtCompound itemTag = new NbtCompound();
        this.storedItem.writeNbt(itemTag);
        tag.put("stored_item", itemTag);
        tag.putInt("bobbing_animation_time", this.bobbingAnimationTime);
        return tag;
    }

    @Override
    public void fromTag(NbtElement tag) {
        if (tag instanceof NbtCompound compound) {
            NbtCompound itemStackNBT = compound.getCompound("stored_item");
            if (!itemStackNBT.isEmpty()) {
                this.storedItem = ItemStack.fromNbt(itemStackNBT);
            }
            this.bobbingAnimationTime = compound.getInt("bobbing_animation_time");
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("item_store"));
    }

    public static PowerConfiguration<ItemStorePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }

    public static @Nullable ItemStorePower findPower(Entity entity, @Nullable Identifier powerID) {
        if (powerID == null) return null;
        if (entity instanceof LivingEntity livingEntity) {
            return PowerHolderComponent.getPowers(livingEntity, ItemStorePower.class).stream()
                    .filter(power -> power.powerID != null && power.powerID.equals(powerID))
                    .findFirst().orElse(null);
        }
        return null;
    }

    public static void registerCondition(Consumer<ConditionConfiguration<CheckStoredItemCondition>> registerFunc) {
        registerFunc.accept(ConditionConfiguration.of(
                ShapeShifterCurseFabric.identifier("check_stored_item"), CheckStoredItemCondition.DATA_FACTORY));
    }

    public static class CheckStoredItemCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<CheckStoredItemCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("id", SerializableDataTypes.IDENTIFIER, null)
                                .add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
                                .add("default", SerializableDataTypes.BOOLEAN, false),
                        data -> new CheckStoredItemCondition(data.getId("id"), data.get("item_condition"), data.getBoolean("default")),
                        (c, sd) -> sd.instance()
                );

        private final Identifier powerId;
        private final Optional<ItemCondition> itemCondition;
        private final boolean defaultVal;

        public CheckStoredItemCondition(Identifier powerId, Optional<ItemCondition> itemCondition, boolean defaultVal) {
            this.powerId = powerId;
            this.itemCondition = itemCondition;
            this.defaultVal = defaultVal;
        }

        @Override
        public boolean test(EntityConditionContext ctx) {
            ItemStorePower power = findPower(ctx.entity(), powerId);
            if (power == null) return defaultVal;
            return itemCondition.map(c -> c.test(power.storedItem)).orElse(defaultVal);
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("check_stored_item"), DATA_FACTORY);
        }
    }

    public static void registerAction(Consumer<ActionConfiguration<EntityActionType>> actionReg, Consumer<?> biActionReg) {
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("gain_store_power_item"), GainStoreItemAction.DATA_FACTORY));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("drop_store_power_item"), DropStoreItemAction.DATA_FACTORY));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("swap_store_power_item"), SwapStoreItemAction.DATA_FACTORY));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("invoke_store_power_item"), InvokeStoreItemAction.DATA_FACTORY));
    }

    public static class GainStoreItemAction extends EntityActionType {
        public static final TypedDataObjectFactory<GainStoreItemAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("id", SerializableDataTypes.IDENTIFIER, null)
                                .add("item", SerializableDataTypes.ITEM_STACK, null)
                                .add("if_no_power_drop", SerializableDataTypes.BOOLEAN, true),
                        data -> new GainStoreItemAction(data.getId("id"), data.get("item"), data.getBoolean("if_no_power_drop")),
                        (a, sd) -> sd.instance()
                );
        private final Identifier powerId;
        private final ItemStack item;
        private final boolean ifNoPowerDrop;

        public GainStoreItemAction(Identifier powerId, ItemStack item, boolean ifNoPowerDrop) {
            this.powerId = powerId; this.item = item; this.ifNoPowerDrop = ifNoPowerDrop;
        }

        @Override
        public void accept(EntityActionContext ctx) {
            ItemStorePower power = findPower(ctx.entity(), powerId);
            if (power != null) {
                power.GainItem(item);
            } else if (ifNoPowerDrop) {
                ctx.entity().dropStack(item);
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("gain_store_power_item"), DATA_FACTORY);
        }
    }

    public static class DropStoreItemAction extends EntityActionType {
        public static final TypedDataObjectFactory<DropStoreItemAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("id", SerializableDataTypes.IDENTIFIER, null)
                                .add("remove_item", SerializableDataTypes.BOOLEAN, false),
                        data -> new DropStoreItemAction(data.getId("id"), data.getBoolean("remove_item")),
                        (a, sd) -> sd.instance()
                );
        private final Identifier powerId;
        private final boolean removeItem;

        public DropStoreItemAction(Identifier powerId, boolean removeItem) {
            this.powerId = powerId; this.removeItem = removeItem;
        }

        @Override
        public void accept(EntityActionContext ctx) {
            ItemStorePower power = findPower(ctx.entity(), powerId);
            if (power == null) return;
            if (removeItem) {
                power.storedItem = ItemStack.EMPTY;
            } else {
                power.DropItem();
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("drop_store_power_item"), DATA_FACTORY);
        }
    }

    public static class SwapStoreItemAction extends EntityActionType {
        public static final TypedDataObjectFactory<SwapStoreItemAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("id", SerializableDataTypes.IDENTIFIER, null)
                                .add("slot", SerializableDataTypes.EQUIPMENT_SLOT, EquipmentSlot.MAINHAND),
                        data -> new SwapStoreItemAction(data.getId("id"), data.get("slot")),
                        (a, sd) -> sd.instance()
                );
        private final Identifier powerId;
        private final EquipmentSlot slot;

        public SwapStoreItemAction(Identifier powerId, EquipmentSlot slot) {
            this.powerId = powerId; this.slot = slot;
        }

        @Override
        public void accept(EntityActionContext ctx) {
            ItemStorePower power = findPower(ctx.entity(), powerId);
            if (power != null) power.SwapItem(slot);
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("swap_store_power_item"), DATA_FACTORY);
        }
    }

    public static class InvokeStoreItemAction extends EntityActionType {
        public static final TypedDataObjectFactory<InvokeStoreItemAction> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("id", SerializableDataTypes.IDENTIFIER, null)
                                .add("action", ItemCondition.DATA_TYPE.optional(), Optional.empty()),
                        data -> new InvokeStoreItemAction(data.getId("id"), data.get("action")),
                        (a, sd) -> sd.instance()
                );
        private final Identifier powerId;
        private final Optional<ItemCondition> itemAction;

        public InvokeStoreItemAction(Identifier powerId, Optional<ItemCondition> itemAction) {
            this.powerId = powerId; this.itemAction = itemAction;
        }

        @Override
        public void accept(EntityActionContext ctx) {
            ItemStorePower power = findPower(ctx.entity(), powerId);
            if (power != null && itemAction.isPresent()) {
                power.InvokeItemAction(stack -> itemAction.get().test(stack));
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return ActionConfiguration.of(ShapeShifterCurseFabric.identifier("invoke_store_power_item"), DATA_FACTORY);
        }
    }

    @Override
    public int getSlot() {
        return this.Slot;
    }

    @Override
    public ItemStack getStack() {
        return this.storedItem;
    }

    @Override
    public float getBobbingAnimationTime() {
        return this.bobbingAnimationTime;
    }
}
