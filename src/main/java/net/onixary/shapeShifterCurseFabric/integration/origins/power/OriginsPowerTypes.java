package net.onixary.shapeShifterCurseFabric.integration.origins.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;

@SuppressWarnings("unchecked")
public class OriginsPowerTypes {

    public static final PowerType<?> LIKE_WATER = new PowerTypeReference<>(Origins.identifier("like_water"));
    public static final PowerType<?> WATER_BREATHING = new PowerTypeReference<>(Origins.identifier("water_breathing"));
    public static final PowerType<?> SCARE_CREEPERS = new PowerTypeReference<>(Origins.identifier("scare_creepers"));
    public static final PowerType<?> WATER_VISION = new PowerTypeReference<>(Origins.identifier("water_vision"));
    public static final PowerType<?> NO_COBWEB_SLOWDOWN = new PowerTypeReference<>(Origins.identifier("no_cobweb_slowdown"));
    public static final PowerType<?> MASTER_OF_WEBS_NO_SLOWDOWN = new PowerTypeReference<>(Origins.identifier("master_of_webs_no_slowdown"));
    public static final PowerType<?> CONDUIT_POWER_ON_LAND = new PowerTypeReference<>(Origins.identifier("conduit_power_on_land"));

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void aliasRegistry(Registry registry, java.util.function.Function<Object, Identifier> getId) {
        registry.forEach(entry -> {
            Object value = entry;
            Identifier apoliId = getId.apply(value);
            if (apoliId != null && "apoli".equals(apoliId.getNamespace())) {
                Identifier originsId = Origins.identifier(apoliId.getPath());
                if (!registry.containsId(originsId)) {
                    Registry.register(registry, originsId, value);
                }
            }
        });
    }

    public static void register() {
        // Register all apoli:* types as origins:* aliases
        // Needed because SSC JSONs use origins: namespace but Origins mod is not installed
        for (var registry : new Registry[]{
            ApoliRegistries.POWER_FACTORY,
            ApoliRegistries.ENTITY_CONDITION,
            ApoliRegistries.BIENTITY_CONDITION,
            ApoliRegistries.ITEM_CONDITION,
            ApoliRegistries.BLOCK_CONDITION,
            ApoliRegistries.DAMAGE_CONDITION,
            ApoliRegistries.FLUID_CONDITION,
            ApoliRegistries.BIOME_CONDITION,
            ApoliRegistries.ENTITY_ACTION,
            ApoliRegistries.ITEM_ACTION,
            ApoliRegistries.BLOCK_ACTION,
            ApoliRegistries.BIENTITY_ACTION,
        }) {
            aliasRegistry(registry, o -> switch (o) {
                case io.github.apace100.apoli.power.factory.PowerFactory pf -> pf.getSerializerId();
                case io.github.apace100.apoli.power.factory.condition.ConditionFactory<?> cf -> cf.getSerializerId();
                case io.github.apace100.apoli.power.factory.action.ActionFactory<?> af -> af.getSerializerId();
                default -> null;
            });
        }

        register(new PowerFactory<>(Origins.identifier("action_on_callback"),
            new SerializableData()
                .add("entity_action_respawned", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_removed", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_gained", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_lost", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_added", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_chosen", ApoliDataTypes.ENTITY_ACTION, null)
                .add("execute_chosen_when_orb", SerializableDataTypes.BOOLEAN, true),
            data ->
                (type, player) -> new OriginsCallbackPower(type, player,
                    (ActionFactory<Entity>.Instance)data.get("entity_action_respawned"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_removed"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_gained"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_lost"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_added"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_chosen"),
                    data.getBoolean("execute_chosen_when_orb")))
            .allowCondition());
    }

    private static void register(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }
}
