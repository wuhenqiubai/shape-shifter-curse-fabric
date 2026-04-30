package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CustomEdiblePower extends PowerType {

    private final FoodComponent foodComponent;
    private final List<Identifier> ItemIdList;

    public static final TypedDataObjectFactory<CustomEdiblePower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("item_id_list", SerializableDataTypes.IDENTIFIERS, null)
                            .add("hunger", SerializableDataTypes.INT, 0)
                            .add("saturation_modifier", SerializableDataTypes.FLOAT, 0.0f)
                            .add("meat", SerializableDataTypes.BOOLEAN, false)
                            .add("always_edible", SerializableDataTypes.BOOLEAN, false)
                            .add("snack", SerializableDataTypes.BOOLEAN, false)
                            .add("status_effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null),
                    (data, condition) -> new CustomEdiblePower(condition, data),
                    (power, sd) -> sd.instance()
            );

    public CustomEdiblePower(Optional<EntityCondition> condition, SerializableData.Instance data) {
        super(condition);
        this.ItemIdList = data.get("item_id_list");
        FoodComponent.Builder foodComponentBuilder = new FoodComponent.Builder()
                .hunger(data.getInt("hunger"))
                .saturationModifier(data.getFloat("saturation_modifier"));
        if (data.getBoolean("meat")) {
            foodComponentBuilder.meat();
        }
        if (data.getBoolean("always_edible")) {
            foodComponentBuilder.alwaysEdible();
        }
        if (data.getBoolean("snack")) {
            foodComponentBuilder.snack();
        }
        List<StatusEffectInstance> effects = data.get("status_effects");
        if (effects != null) {
            for (StatusEffectInstance effect : effects) {
                foodComponentBuilder.statusEffect(effect, 1.0f);
            }
        }
        this.foodComponent = foodComponentBuilder.build();
    }

    public List<Identifier> getItemIdList() {
        return this.ItemIdList;
    }

    public FoodComponent getFoodComponent() {
        return this.foodComponent;
    }

    public static void OnClientTick(PlayerEntity player) {
        if (player.age % 100 == 0) {
            CustomEdibleUtils.ReloadPlayerCustomEdible(player);
        }
    }

    public static void OnServerTick(ServerPlayerEntity player) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return;
        }
        if (player.age % 100 == 0) {
            CustomEdibleUtils.ReloadPlayerCustomEdible(player);
        }
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("custom_edible"));
    }

    public static PowerConfiguration<CustomEdiblePower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}