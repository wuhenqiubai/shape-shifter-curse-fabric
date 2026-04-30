package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyFoodHealPower extends PowerType {

    private int LastModifyFoodHealTimer = 0;
    private float RemainFoodHealTime = 0.0f;
    private final float FoodTimerAddAmount;
    private final int ModifyFoodTimerTickRate;

    public static final TypedDataObjectFactory<ModifyFoodHealPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("food_timer_add_amount", SerializableDataTypes.FLOAT, 1.0f)
                            .add("modify_food_timer_tick_rate", SerializableDataTypes.INT, 20),
                    (data, condition) -> new ModifyFoodHealPower(
                            data.getFloat("food_timer_add_amount"),
                            data.getInt("modify_food_timer_tick_rate"),
                            condition),
                    (power, sd) -> sd.instance()
                            .set("food_timer_add_amount", power.FoodTimerAddAmount)
                            .set("modify_food_timer_tick_rate", power.ModifyFoodTimerTickRate)
            );

    public ModifyFoodHealPower(float FoodTimerAddAmount, int ModifyFoodTimerTickRate, Optional<EntityCondition> condition) {
        super(condition);
        this.FoodTimerAddAmount = FoodTimerAddAmount;
        this.ModifyFoodTimerTickRate = ModifyFoodTimerTickRate;
    }

    public int ProcessFoodTick(int FoodTick) {
        this.LastModifyFoodHealTimer++;
        if (this.LastModifyFoodHealTimer >= this.ModifyFoodTimerTickRate) {
            this.LastModifyFoodHealTimer = 0;
            this.RemainFoodHealTime += this.FoodTimerAddAmount;
            return this.ApplyFoodTick(FoodTick);
        } else {
            return FoodTick;
        }
    }

    public int ApplyFoodTick(int FoodTick) {
        int FoodTickerAmount = (int) this.RemainFoodHealTime;
        if (FoodTickerAmount != 0) {
            this.RemainFoodHealTime -= FoodTickerAmount;
            return Math.max(FoodTick + FoodTickerAmount, 0);
        } else {
            return FoodTick;
        }
    }

    public boolean CanApply(PlayerEntity player) {
        return player.getHungerManager().getFoodLevel() >= 18 && player.canFoodHeal();
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("modify_food_heal"));
    }

    public static PowerConfiguration<ModifyFoodHealPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}