package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class AdditionalEntityActions {

    public static final ActionConfiguration<AddInstinctAction> ADD_INSTINCT = registerEntityAction(AddInstinctAction.createConfig(ShapeShifterCurseFabric.identifier("add_instinct")));
    public static final ActionConfiguration<SetFallingDistanceAction> SET_FALLING_DISTANCE = registerEntityAction(SetFallingDistanceAction.createConfig(ShapeShifterCurseFabric.identifier("set_falling_distance")));
    public static final ActionConfiguration<SpawnParticlesInCircleAction> SPAWN_PARTICLES_IN_CIRCLE = registerEntityAction(SpawnParticlesInCircleAction.createConfig(ShapeShifterCurseFabric.identifier("spawn_particles_in_circle")));
    public static final ActionConfiguration<SummonMinionWolfNearbyAction.SummonAction> SUMMON_MINION_WOLF = registerEntityAction(SummonMinionWolfNearbyAction.createConfig(ShapeShifterCurseFabric.identifier("summon_anubis_wolf_minion")));
    public static final ActionConfiguration<SummonMinionWolfNearbyAction.SummonBIAction> SUMMON_MINION_WOLF_BI;

    static {
        SUMMON_MINION_WOLF_BI = SummonMinionWolfNearbyAction.createBIConfig(ShapeShifterCurseFabric.identifier("bi_summon_anubis_wolf_minion"));
        registerBiEntityAction(SUMMON_MINION_WOLF_BI);
    }
    public static final ActionConfiguration<TransformAction.TransformToFormAction> TRANSFORM_TO_FORM = registerEntityAction(ActionConfiguration.of(ShapeShifterCurseFabric.identifier("transform_to_form"), TransformAction.TransformToFormAction.DATA_FACTORY));
    public static final ActionConfiguration<TransformAction.GiveCustomTransformEffectAction> GIVE_CUSTOM_TRANSFORM_EFFECT = registerEntityAction(ActionConfiguration.of(ShapeShifterCurseFabric.identifier("give_custom_transform_effect"), TransformAction.GiveCustomTransformEffectAction.DATA_FACTORY));
    public static final ActionConfiguration<FireArrowAction> FIRE_ARROW = registerEntityAction(FireArrowAction.createConfig(ShapeShifterCurseFabric.identifier("fire_arrow")));
    public static final ActionConfiguration<WebBridgeAction.WebBridgeEntityAction> WEB_BRIDGE = registerEntityAction(ActionConfiguration.of(ShapeShifterCurseFabric.identifier("web_bridge"), WebBridgeAction.WebBridgeEntityAction.DATA_FACTORY));
    public static final ActionConfiguration<WebBridgeAction.FireWebBulletAction> FIRE_WEB_BULLET = registerEntityAction(ActionConfiguration.of(ShapeShifterCurseFabric.identifier("fire_web_bullet"), WebBridgeAction.FireWebBulletAction.DATA_FACTORY));
    public static final ActionConfiguration<TrinketsConditionAction.DropAccessoryAction> DROP_ACCESSORY = registerEntityAction(ActionConfiguration.of(ShapeShifterCurseFabric.identifier("drop_accessory"), TrinketsConditionAction.DropAccessoryAction.DATA_FACTORY));
    public static final ActionConfiguration<TrinketsConditionAction.InvokeAccessoryAction> INVOKE_ACCESSORY = registerEntityAction(ActionConfiguration.of(ShapeShifterCurseFabric.identifier("invoke_accessory"), TrinketsConditionAction.InvokeAccessoryAction.DATA_FACTORY));
    public static final ActionConfiguration<ExplosionDamageEntityAction> EXPLOSION_DAMAGE_ENTITY = registerEntityAction(ExplosionDamageEntityAction.createConfig(ShapeShifterCurseFabric.identifier("explosion_damage_entity")));

    public static void register() {
        TransformAction.registerAction(AdditionalEntityActions::registerEntityAction, (ActionConfiguration<? extends BiEntityActionType> c) -> registerBiEntityAction(c));
        ExplosionDamageEntityAction.register(AdditionalEntityActions::registerEntityAction);
        SummonMinionWolfNearbyAction.register(AdditionalEntityActions::registerEntityAction, (ActionConfiguration<? extends BiEntityActionType> c) -> registerBiEntityAction(c));
        PlayPowerAnimationAction.register(AdditionalEntityActions::registerEntityAction, (ActionConfiguration<? extends BiEntityActionType> c) -> registerBiEntityAction(c));
        TrinketsConditionAction.registerAction(AdditionalEntityActions::registerEntityAction, (ActionConfiguration<? extends BiEntityActionType> c) -> registerBiEntityAction(c));
        ManaUtilsApoli.registerAction(AdditionalEntityActions::registerEntityAction, (ActionConfiguration<? extends BiEntityActionType> c) -> registerBiEntityAction(c));
        FireArrowAction.registerAction(AdditionalEntityActions::registerEntityAction, (ActionConfiguration<? extends BiEntityActionType> c) -> registerBiEntityAction(c));
        WebBridgeAction.registerAction(AdditionalEntityActions::registerEntityAction, (ActionConfiguration<? extends BiEntityActionType> c) -> registerBiEntityAction(c));
        ItemStorePower.registerAction(AdditionalEntityActions::registerEntityAction, (ActionConfiguration<? extends BiEntityActionType> c) -> registerBiEntityAction(c));
        ItemCooldownCA.registerAction(AdditionalEntityActions::registerEntityAction, (ActionConfiguration<? extends BiEntityActionType> c) -> registerBiEntityAction(c));
    }

    @SuppressWarnings("unchecked")
    public static <T extends EntityActionType> ActionConfiguration<T> registerEntityAction(ActionConfiguration<T> config) {
        ActionConfiguration<EntityActionType> casted = (ActionConfiguration<EntityActionType>) config;
        return (ActionConfiguration<T>) Registry.register(ApoliRegistries.ENTITY_ACTION_TYPE, casted.id(), casted);
    }

    @SuppressWarnings("unchecked")
    public static void registerBiEntityAction(ActionConfiguration<? extends BiEntityActionType> config) {
        Registry.register(ApoliRegistries.BIENTITY_ACTION_TYPE, config.id(),
                (ActionConfiguration<BiEntityActionType>) (Object) config);
    }
}