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
        register(AddSustainedInstinctPower.getFactory());
        register(AddImmediateInstinctPower.getFactory());
        register(CrawlingPower.getFactory());
        register(ScalePower.getFactory());
        register(LevitatePower.getFactory());
        register(AttractByEntityPower.getFactory());
        register(LootingPower.createFactory());
        register(ProjectileDodgePower.createFactory());
        register(WaterFlexibilityPower.createFactory());
        register(AlwaysSweepingPower.createFactory());
        register(FallingProtectionPower.createFactory());
        register(EnhancedFallingAttackPower.createFactory());
        register(TripleJumpPower.createFactory());
        register(PowderSnowWalkerPower.createFactory());
        register(FoxFriendlyPower.createFactory());
        register(BurnDamageModifierPower.createFactory());
        register(CriticalDamageModifierPower.createFactory());
        register(SnowballBlockTransformPower.createFactory());
        register(BatBlockAttachPower.createFactory());
        register(ActionOnJumpPower.createFactory());
        register(NoRenderArmPower.createFactory());
        register(CustomEdiblePower.createFactory());
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
        register(DelayAttributePower.createFactory());
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
        register(ManaTypePower.createFactory());
        register(ManaAttributePower.createFactory());
        register(ConditionedManaAttributePower.createFactory());
        register(HissPhantomPower.createFactory());
        register(BypassesLandingEffectsPower.createFactory());
        register(BypassesSteppingEffectsPower.createFactory());
        register(FormCameraBobbingPower.createFactory());
        register(SlowdownPercentPower.createFactory());
        register(ChargePower.createFactory());
        register(ItemStorePower.createFactory());
        register(ModifyFallDamagePower.createFactory());
        register(VirtualShieldPower.createFactory());
        register(RenderTrinketsSlotPower.createFactory());
    }

    private static <T extends PowerType> PowerConfiguration<T> register(PowerConfiguration<T> config) {
        return Registry.register(ApoliRegistries.POWER_TYPE, config.id(), config);
    }
}
