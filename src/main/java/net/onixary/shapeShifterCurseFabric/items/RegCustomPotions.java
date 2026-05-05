package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;
import static net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects.FEED_EFFECT;
import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusPotionEffect.*;

public class RegCustomPotions {
    public static final Potion MOONDUST_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "moondust_potion")), new Potion());
    public static final Potion BAT_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_bat_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_BAT_0_POTION), 3600)));
    public static final Potion AXOLOTL_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_axolotl_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_AXOLOTL_0_POTION), 3600)));
    public static final Potion OCELOT_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_ocelot_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_OCELOT_0_POTION), 3600)));
    public static final Potion FAMILIAR_FOX_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_familiar_fox_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_FAMILIAR_FOX_0_POTION), 3600)));
    public static final Potion SNOW_FOX_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_snow_fox_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_SNOW_FOX_0_POTION), 3600)));
    public static final Potion ANUBIS_WOLF_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_anubis_wolf_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_ANUBIS_WOLF_0_POTION), 3600)));
    public static final Potion SPIDER_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_spider_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_SPIDER_0_POTION), 3600)));

    public static final Potion ALLEY_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_allay_sp_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_ALLAY_SP_POTION), 3600)));
    public static final Potion FERAL_CAT_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_feral_cat_sp_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_FERAL_CAT_SP_POTION), 3600)));
    public static final Potion CUSTOM_STATUE_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_custom_statue_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_CUSTOM_STATUE_POTION), 3600)));

    /* 未支持数据包时代的占位形态 现在可以使用数据添加形态了
    // custom empty forms
    public static final Potion ALPHA_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_alpha_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_ALPHA_0_POTION), 3600)));
    public static final Potion BETA_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_beta_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_BETA_0_POTION), 3600)));
    public static final Potion GAMMA_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_gamma_0_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_GAMMA_0_POTION), 3600)));
    public static final Potion OMEGA_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_omega_sp_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_OMEGA_SP_POTION), 3600)));
    public static final Potion PSI_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_psi_sp_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_PSI_SP_POTION), 3600)));
    public static final Potion CHI_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_chi_sp_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_CHI_SP_POTION), 3600)));
    public static final Potion PHI_FORM_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "to_phi_sp_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(TO_PHI_SP_POTION), 3600)));
     */
    // other custom potions
    // feed potion can only be obtained via familiar_fox_2 and familiar_fox_3, no recipe
    public static final Potion FEED_POTION =
            Registry.register(Registries.POTION, RegistryKey.of(RegistryKeys.POTION, Identifier.of(MOD_ID, "feed_potion")), new Potion(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(FEED_EFFECT), 3600)));

    public static void registerPotions(){

    }

    public static void registerPotionsRecipes(){
        // BrewingRecipeRegistry disabled: Recipe is final class in 1.21
        // Brewing recipes must be defined via JSON data files
    
    }
}
