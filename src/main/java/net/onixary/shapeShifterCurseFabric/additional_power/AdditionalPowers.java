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
        register(ScalePower.getFactory());
        register(LevitatePower.createFactory(ShapeShifterCurseFabric.identifier("levitate")));
        register(AttractByEntityPower.getFactory());
        register(LootingPower.createFactory());
        register(ProjectileDodgePower.createFactory());
        register(WaterFlexibilityPower.createFactory());
        register(AlwaysSweepingPower.createFactory(ShapeShifterCurseFabric.identifier("always_sweeping")));
        register(FallingProtectionPower.createFactory());
        register(EnhancedFallingAttackPower.createFactory());
        register(TripleJumpPower.createFactory());
        register(PowderSnowWalkerPower.createFactory());
        register(FoxFriendlyPower.createFactory());
        register(BurnDamageModifierPower.createFactory());
        register(CriticalDamageModifierPower.createFactory());
        register(SnowballBlockTransformPower.createFactory());
        register(BatBlockAttachPower.createFactory(ShapeShifterCurseFabric.identifier("bat_block_attach")));
        register(ActionOnJumpPower.createFactory());
        register(NoRenderArmPower.createFactory());
        register(CustomEdiblePower.createFactory(ShapeShifterCurseFabric.identifier("custom_edible")));
        register(NoStepSoundPower.createFactory());
        register(PillagerFriendlyPower.createFactory());
        register(PreventBerryEffectPower.createFactory());
        register(WitchFriendlyPower.createFactory());
        register(ScareVillagerPower.createFactory());
        register(ModifyPotionStackPower.createFactory());
        register(BreathingUnderWaterPower.createFactory());
        register(HoldBreathPower.createFactory());
        register(CustomWaterBreathingPower.createFactory());
        register(ConditionedModifySlipperinessPower.createFactory());
        register(ActionOnSprintingToSneakingPower.createFactory());
        register(ModifyStepHeightPower.createFactory());
        register(KeepSneakingPower.createFactory());
        register(DelayAttributePower.createFactory(ShapeShifterCurseFabric.identifier("delay_attribute")));
        register(AlwaysSprintSwimmingPower.createFactory());
        register(ActionOnSplashPotionTakeEffect.createFactory());
        register(ConditionScalePower.createFactory());
        register(SneakingJumpClashPower.createFactory());
        register(InWaterSpeedModifierPower.createFactory());
        register(VirtualTotemPower.createFactory());
        register(ModifyInstantHealthPower.createFactory());
        register(ModifyInstantDamagePower.createFactory());
        register(SoulSpeedPower.createFactory());
        register(TWolfFriendlyPower.createFactory());
        register(ModifyFoodHealPower.createFactory());
        register(ModifyEntityLootPower.createFactory());
        register(ModifyBlockDropPower.createFactory());
        register(ActionOnEntityInRangePower.createFactory());
        register(ApplyEffectPower.createFactory());
        register(OptionalEffectImmunityPower.createFactory());
        register(ManaTypePower.createFactory(ShapeShifterCurseFabric.identifier("mana_type_power")));
        register(ManaAttributePower.createFactory());
        register(ConditionedManaAttributePower.createFactory(ShapeShifterCurseFabric.identifier("conditioned_mana_attribute")));
        register(HissPhantomPower.createFactory());
        register(BypassesLandingEffectsPower.createFactory());
        register(BypassesSteppingEffectsPower.createFactory());
        register(FormCameraBobbingPower.createFactory());
        register(SlowdownPercentPower.createFactory());
        register(ChargePower.createFactory(ShapeShifterCurseFabric.identifier("charge_action")));
        register(ItemStorePower.createFactory(ShapeShifterCurseFabric.identifier("item_store")));
        register(ModifyFallDamagePower.createFactory());
        register(VirtualShieldPower.createFactory());
        register(RenderTrinketsSlotPower.createFactory(ShapeShifterCurseFabric.identifier("render_accessory_slot")));
    }

    private static <T extends PowerType> PowerConfiguration<T> register(PowerConfiguration<T> config) {
        return Registry.register(ApoliRegistries.POWER_TYPE, config.id(), config);
    }
}
