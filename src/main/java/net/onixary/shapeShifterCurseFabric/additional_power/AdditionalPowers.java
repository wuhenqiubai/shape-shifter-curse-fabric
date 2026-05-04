package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.PowerReference;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class AdditionalPowers {
    public static final PowerReference SCARE_SKELETON = PowerReference.of(ShapeShifterCurseFabric.identifier("scare_skeleton"));
    public static final PowerReference CAN_LOOT_SPIDER_FLUID_COCOON = PowerReference.of(ShapeShifterCurseFabric.identifier("can_loot_spider_fluid_cocoon"));
    public static final PowerReference SPIDER_FRIENDLY = PowerReference.of(ShapeShifterCurseFabric.identifier("spider_friendly"));
    public static final PowerReference HOSTILE_IRON_GOLEM = PowerReference.of(ShapeShifterCurseFabric.identifier("hostile_iron_golem"));

public static void register() {
        register(AddSustainedInstinctPower.createFactory(ShapeShifterCurseFabric.identifier("add_sustained_instinct")));
        register(AddImmediateInstinctPower.createFactory(ShapeShifterCurseFabric.identifier("add_immediate_instinct")));
        register(CrawlingPower.createFactory(ShapeShifterCurseFabric.identifier("crawling")));
        register(ScalePower.createFactory(ShapeShifterCurseFabric.identifier("scale")));
        register(LevitatePower.createFactory(ShapeShifterCurseFabric.identifier("levitate")));
        register(AttractByEntityPower.createFactory(ShapeShifterCurseFabric.identifier("attract_by_entity")));
        register(LootingPower.createFactory(ShapeShifterCurseFabric.identifier("looting")));
        register(ProjectileDodgePower.createFactory(ShapeShifterCurseFabric.identifier("projectile_dodge")));
        register(WaterFlexibilityPower.createFactory(ShapeShifterCurseFabric.identifier("water_flexibility")));
        register(AlwaysSweepingPower.createFactory(ShapeShifterCurseFabric.identifier("always_sweeping")));
        register(FallingProtectionPower.createFactory(ShapeShifterCurseFabric.identifier("falling_protection")));
        register(EnhancedFallingAttackPower.createFactory(ShapeShifterCurseFabric.identifier("enhanced_falling_attack")));
        register(TripleJumpPower.createFactory(ShapeShifterCurseFabric.identifier("triple_jump")));
        register(PowderSnowWalkerPower.createFactory(ShapeShifterCurseFabric.identifier("powder_snow_walker")));
        register(FoxFriendlyPower.createFactory(ShapeShifterCurseFabric.identifier("fox_friendly")));
        register(BurnDamageModifierPower.createFactory(ShapeShifterCurseFabric.identifier("burn_damage_modifier")));
        register(CriticalDamageModifierPower.createFactory(ShapeShifterCurseFabric.identifier("critical_damage_modifier")));
        register(SnowballBlockTransformPower.createFactory(ShapeShifterCurseFabric.identifier("snowball_block_transform")));
        register(BatBlockAttachPower.createFactory(ShapeShifterCurseFabric.identifier("bat_block_attach")));
        register(ActionOnJumpPower.createFactory(ShapeShifterCurseFabric.identifier("action_on_jump")));
        register(NoRenderArmPower.createFactory(ShapeShifterCurseFabric.identifier("no_render_arm")));
        register(CustomEdiblePower.createFactory(ShapeShifterCurseFabric.identifier("custom_edible")));
        register(NoStepSoundPower.createFactory(ShapeShifterCurseFabric.identifier("no_step_sound")));
        register(PillagerFriendlyPower.createFactory(ShapeShifterCurseFabric.identifier("pillager_friendly")));
        register(PreventBerryEffectPower.createFactory(ShapeShifterCurseFabric.identifier("prevent_berry_effect")));
        register(WitchFriendlyPower.createFactory(ShapeShifterCurseFabric.identifier("witch_friendly")));
        register(ScareVillagerPower.createFactory(ShapeShifterCurseFabric.identifier("scare_villager")));
        register(ModifyPotionStackPower.createFactory(ShapeShifterCurseFabric.identifier("modify_potion_stack")));
        register(BreathingUnderWaterPower.createFactory(ShapeShifterCurseFabric.identifier("breathing_under_water")));
        register(HoldBreathPower.createFactory(ShapeShifterCurseFabric.identifier("hold_breath")));
        register(CustomWaterBreathingPower.createFactory(ShapeShifterCurseFabric.identifier("custom_water_breathing")));
        register(ConditionedModifySlipperinessPower.createFactory(ShapeShifterCurseFabric.identifier("conditioned_modify_slipperiness")));
        register(ActionOnSprintingToSneakingPower.createFactory(ShapeShifterCurseFabric.identifier("action_on_sprinting_to_sneaking")));
        register(ModifyStepHeightPower.createFactory(ShapeShifterCurseFabric.identifier("modify_step_height")));
        register(KeepSneakingPower.createFactory(ShapeShifterCurseFabric.identifier("keep_sneaking")));
        register(DelayAttributePower.createFactory(ShapeShifterCurseFabric.identifier("delay_attribute")));
        register(AlwaysSprintSwimmingPower.createFactory(ShapeShifterCurseFabric.identifier("always_sprint_swimming")));
        register(ActionOnSplashPotionTakeEffect.createFactory(ShapeShifterCurseFabric.identifier("action_on_splash_potion_take_effect")));
        register(ConditionScalePower.createFactory(ShapeShifterCurseFabric.identifier("condition_scale")));
        register(SneakingJumpClashPower.createFactory(ShapeShifterCurseFabric.identifier("sneaking_jump_clash")));
        register(InWaterSpeedModifierPower.createFactory(ShapeShifterCurseFabric.identifier("in_water_speed_modifier")));
        register(VirtualTotemPower.createFactory(ShapeShifterCurseFabric.identifier("virtual_totem")));
        register(ModifyInstantHealthPower.createFactory(ShapeShifterCurseFabric.identifier("modify_instant_health")));
        register(ModifyInstantDamagePower.createFactory(ShapeShifterCurseFabric.identifier("modify_instant_damage")));
        register(SoulSpeedPower.createFactory(ShapeShifterCurseFabric.identifier("soul_speed")));
        register(TWolfFriendlyPower.createFactory(ShapeShifterCurseFabric.identifier("twolf_friendly")));
        register(ModifyFoodHealPower.createFactory(ShapeShifterCurseFabric.identifier("modify_food_heal")));
        register(ModifyEntityLootPower.createFactory(ShapeShifterCurseFabric.identifier("modify_entity_loot")));
        register(ModifyBlockDropPower.createFactory(ShapeShifterCurseFabric.identifier("modify_block_drop")));
        register(PowerConfiguration.dataFactory(
                ShapeShifterCurseFabric.identifier("action_on_entity_in_range"),
                ActionOnEntityInRangePower.createFactory()));
        register(ApplyEffectPower.createFactory(ShapeShifterCurseFabric.identifier("apply_effect")));
        register(OptionalEffectImmunityPower.createFactory(ShapeShifterCurseFabric.identifier("optional_effect_immunity")));
        register(ManaTypePower.createFactory(ShapeShifterCurseFabric.identifier("mana_type_power")));
        register(ManaAttributePower.createFactory(ShapeShifterCurseFabric.identifier("mana_attribute")));
        register(ConditionedManaAttributePower.createFactory(ShapeShifterCurseFabric.identifier("conditioned_mana_attribute")));
        register(HissPhantomPower.createFactory(ShapeShifterCurseFabric.identifier("hiss_phantom")));
        register(BypassesLandingEffectsPower.createFactory(ShapeShifterCurseFabric.identifier("bypasses_landing_effects")));
        register(BypassesSteppingEffectsPower.createFactory(ShapeShifterCurseFabric.identifier("bypasses_stepping_effects")));
        register(FormCameraBobbingPower.createFactory(ShapeShifterCurseFabric.identifier("form_camera_bobbing")));
        register(SlowdownPercentPower.createFactory(ShapeShifterCurseFabric.identifier("slowdown_percent")));
        register(ChargePower.createFactory(ShapeShifterCurseFabric.identifier("charge_action")));
        register(ItemStorePower.createFactory(ShapeShifterCurseFabric.identifier("item_store")));
        register(ModifyFallDamagePower.createFactory(ShapeShifterCurseFabric.identifier("modify_fall_damage")));
        register(VirtualShieldPower.createFactory(ShapeShifterCurseFabric.identifier("virtual_shield")));
        register(RenderTrinketsSlotPower.createFactory(ShapeShifterCurseFabric.identifier("render_accessory_slot")));
    }

    @SuppressWarnings("unchecked")
    private static <T extends PowerType> PowerConfiguration<T> register(PowerConfiguration<T> config) {
        Registry.register(ApoliRegistries.POWER_TYPE, config.id(),
                (PowerConfiguration<PowerType>) (Object) config);
        return config;
    }
}
