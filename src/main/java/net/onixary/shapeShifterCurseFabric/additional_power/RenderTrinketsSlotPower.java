package net.onixary.shapeShifterCurseFabric.additional_power;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryUtils;
import net.onixary.shapeShifterCurseFabric.items.accessory.CurioUtils;
import net.onixary.shapeShifterCurseFabric.render.tech.ItemStorePowerRender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

import static net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryUtils.calcAutoMod;

public class RenderTrinketsSlotPower extends PowerType implements ItemStorePowerRender.itemStorePowerRenderInterface {
    private final String accessoryMod;
    private final int slot;
    private final String TGroup;
    private final String TSlot;
    private final int TSlotIndex;

    public static final TypedDataObjectFactory<RenderTrinketsSlotPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("accessory_mod", SerializableDataTypes.STRING, "trinkets")
                            .add("accessory_group", SerializableDataTypes.STRING, "")
                            .add("accessory_slot", SerializableDataTypes.STRING, "")
                            .add("accessory_slot_index", SerializableDataTypes.INT, 0)
                            .add("slot", SerializableDataTypes.INT, 0),
                    (data, condition) -> new RenderTrinketsSlotPower(
                            condition,
                            calcAutoMod(data.getString("accessory_mod")),
                            data.getInt("slot"),
                            data.getString("accessory_group"),
                            data.getString("accessory_slot"),
                            data.getInt("accessory_slot_index")
                    ),
                    (power, sd) -> sd.instance()
            );

    public RenderTrinketsSlotPower(Optional<EntityCondition> condition, String accessoryMod, int slot,
                                   String TGroup, String TSlot, int TSlotIndex) {
        super(condition);
        this.accessoryMod = accessoryMod;
        this.slot = slot;
        this.TGroup = TGroup;
        this.TSlot = TSlot;
        this.TSlotIndex = TSlotIndex;
    }

    @Override
    public int getSlot() {
        return this.slot;
    }

    @Override
    public ItemStack getStack() {
        LivingEntity entity = getHolder();
        switch (accessoryMod) {
            case "trinkets":
                if (AccessoryUtils.LOADED_Trinkets) {
                    Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
                    if (component.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    Map<String, TrinketInventory> groupInv = component.get().getInventory().get(TGroup);
                    if (groupInv == null) {
                        return ItemStack.EMPTY;
                    }
                    TrinketInventory inv = groupInv.get(TSlot);
                    if (inv == null) {
                        return ItemStack.EMPTY;
                    }
                    return inv.getStack(TSlotIndex);
                }
                return ItemStack.EMPTY;
            case "curios":
                if (AccessoryUtils.LOADED_Curios) {
                    return CurioUtils.getEntitySlot(entity, TSlot).get(TSlotIndex);
                }
                return ItemStack.EMPTY;
            case "none":
                return ItemStack.EMPTY;
            default:
                ShapeShifterCurseFabric.LOGGER.error("[render_accessory_slot] accessory_mod is not valid");
                return ItemStack.EMPTY;
        }
    }

    @Override
    public float getBobbingAnimationTime() {
        return 0;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("render_accessory_slot"));
    }

    public static PowerConfiguration<RenderTrinketsSlotPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}