package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.util.AttackEntityDataTracker;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.jetbrains.annotations.NotNull;

public class AdditionalEntityConditions {

    public static final ConditionConfiguration<DiggingBareHandCondition> BAREHAND_DIGGING =
            registerTyped(ShapeShifterCurseFabric.identifier("barehand_digging"), DiggingBareHandCondition.DATA_FACTORY);
    public static final ConditionConfiguration<ChanceCondition> CHANCE =
            registerTyped(ShapeShifterCurseFabric.identifier("chance"), ChanceCondition.DATA_FACTORY);
    public static final ConditionConfiguration<JumpEventCondition> JUMP_EVENT =
            registerTyped(ShapeShifterCurseFabric.identifier("jump_event"), JumpEventCondition.DATA_FACTORY);
    public static final ConditionConfiguration<MustCrawlingCondition> MUST_CRAWLING =
            registerTyped(ShapeShifterCurseFabric.identifier("must_crawling"), MustCrawlingCondition.DATA_FACTORY);
    public static final ConditionConfiguration<CanRenderGuiCondition> CAN_RENDER_GUI =
            registerTyped(ShapeShifterCurseFabric.identifier("can_render_gui"), CanRenderGuiCondition.DATA_FACTORY);
    public static final ConditionConfiguration<EnableRandomSoundCondition> ENABLE_RANDOM_SOUND =
            registerTyped(ShapeShifterCurseFabric.identifier("enable_random_sound"), EnableRandomSoundCondition.DATA_FACTORY);
    public static final ConditionConfiguration<LastAttackWitchTimeCondition> LAST_ATTACK_WITCH_TIME =
            registerTyped(ShapeShifterCurseFabric.identifier("last_attack_witch_time"), LastAttackWitchTimeCondition.DATA_FACTORY);
    public static final ConditionConfiguration<LastAttackPillagerTimeCondition> LAST_ATTACK_PILLAGER_TIME =
            registerTyped(ShapeShifterCurseFabric.identifier("last_attack_pillager_time"), LastAttackPillagerTimeCondition.DATA_FACTORY);
    public static final ConditionConfiguration<IsSleepCondition> IS_SLEEP =
            registerTyped(ShapeShifterCurseFabric.identifier("is_sleep"), IsSleepCondition.DATA_FACTORY);
    public static final ConditionConfiguration<TrinketsConditionAction.EquipAccessoryCondition> EQUIP_ACCESSORY =
            registerSpecific(ShapeShifterCurseFabric.identifier("equip_accessory"), TrinketsConditionAction.EquipAccessoryCondition.DATA_FACTORY);
    public static final ConditionConfiguration<TrinketsConditionAction.CheckAccessoryCondition> CHECK_ACCESSORY =
            registerSpecific(ShapeShifterCurseFabric.identifier("check_accessory"), TrinketsConditionAction.CheckAccessoryCondition.DATA_FACTORY);

    public static void register() {
        TrinketsConditionAction.registerCondition(AdditionalEntityConditions::registerSpecific);
        ManaUtilsApoli.registerCondition(AdditionalEntityConditions::register);
        ItemStorePower.registerCondition(AdditionalEntityConditions::registerSpecific);
        ItemCooldownCA.registerCondition(AdditionalEntityConditions::registerSpecific);
    }

    @SuppressWarnings("unchecked")
    private static <T extends EntityConditionType> ConditionConfiguration<T> registerTyped(
            net.minecraft.util.Identifier id, TypedDataObjectFactory<T> factory) {
        ConditionConfiguration<T> config = ConditionConfiguration.<T>of(id, factory);
        Registry.register(ApoliRegistries.ENTITY_CONDITION_TYPE, id,
                (ConditionConfiguration<EntityConditionType>) (Object) config);
        return config;
    }

    @SuppressWarnings("unchecked")
    private static <T extends EntityConditionType> void register(
            ConditionConfiguration<T> config) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION_TYPE, config.id(),
                (ConditionConfiguration<EntityConditionType>) (Object) config);
    }

    @SuppressWarnings("unchecked")
    private static <T extends EntityConditionType> ConditionConfiguration<T> registerSpecific(
            net.minecraft.util.Identifier id, TypedDataObjectFactory<T> factory) {
        ConditionConfiguration<T> config = ConditionConfiguration.of(id, factory);
        Registry.register(ApoliRegistries.ENTITY_CONDITION_TYPE, id,
                (ConditionConfiguration<EntityConditionType>) (Object) config);
        return config;
    }

    @SuppressWarnings("unchecked")
    private static <T extends EntityConditionType> void registerSpecific(
            ConditionConfiguration<T> config) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION_TYPE, config.id(),
                (ConditionConfiguration<EntityConditionType>) (Object) config);
    }

    public static class CanRenderGuiCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<CanRenderGuiCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData(),
                        data -> new CanRenderGuiCondition(),
                        (c, sd) -> sd.instance()
                );

        @Override
        public boolean test(EntityConditionContext ctx) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
                return ClientUtils.CanDisplayGUI();
            return true;
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() { return CAN_RENDER_GUI; }
    }

    public static class EnableRandomSoundCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<EnableRandomSoundCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData(),
                        data -> new EnableRandomSoundCondition(),
                        (c, sd) -> sd.instance()
                );

        @Override
        public boolean test(EntityConditionContext ctx) {
            if (ctx.entity() instanceof PlayerEntity player) {
                PlayerSkinComponent skin = RegPlayerSkinComponent.SKIN_SETTINGS.get(player);
                return skin.isEnableFormRandomSound();
            }
            return true;
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() { return ENABLE_RANDOM_SOUND; }
    }

    public static class LastAttackWitchTimeCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<LastAttackWitchTimeCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("comparison", ApoliDataTypes.COMPARISON)
                                .add("compare_to", SerializableDataTypes.INT, 0),
                        data -> new LastAttackWitchTimeCondition(
                                data.get("comparison"), data.getInt("compare_to")),
                        (c, sd) -> sd.instance()
                );

        private final Comparison comparison;
        private final int compareTo;

        public LastAttackWitchTimeCondition(Comparison comparison, int compareTo) {
            this.comparison = comparison;
            this.compareTo = compareTo;
        }

        @Override
        public boolean test(EntityConditionContext ctx) {
            if (!(ctx.entity() instanceof PlayerEntity player) || comparison == null) return false;
            long lastAttack = AttackEntityDataTracker.lastAttackWitchTimeMap
                    .getOrDefault(player.getUuid(), Long.MIN_VALUE / 16);
            long trueLastAttack = player.getWorld().getTime() - lastAttack;
            return comparison.compare(trueLastAttack, compareTo);
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() { return LAST_ATTACK_WITCH_TIME; }
    }

    public static class LastAttackPillagerTimeCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<LastAttackPillagerTimeCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("comparison", ApoliDataTypes.COMPARISON)
                                .add("compare_to", SerializableDataTypes.INT, 0),
                        data -> new LastAttackPillagerTimeCondition(
                                data.get("comparison"), data.getInt("compare_to")),
                        (c, sd) -> sd.instance()
                );

        private final Comparison comparison;
        private final int compareTo;

        public LastAttackPillagerTimeCondition(Comparison comparison, int compareTo) {
            this.comparison = comparison;
            this.compareTo = compareTo;
        }

        @Override
        public boolean test(EntityConditionContext ctx) {
            if (!(ctx.entity() instanceof PlayerEntity player) || comparison == null) return false;
            long lastAttack = AttackEntityDataTracker.lastAttackPillagerTimeMap
                    .getOrDefault(player.getUuid(), Long.MIN_VALUE / 16);
            long trueLastAttack = player.getWorld().getTime() - lastAttack;
            return comparison.compare(trueLastAttack, compareTo);
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() { return LAST_ATTACK_PILLAGER_TIME; }
    }

    public static class IsSleepCondition extends EntityConditionType {
        public static final TypedDataObjectFactory<IsSleepCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData(),
                        data -> new IsSleepCondition(),
                        (c, sd) -> sd.instance()
                );

        @Override
        public boolean test(EntityConditionContext ctx) {
            return ctx.entity() instanceof PlayerEntity p && p.isSleeping();
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() { return IS_SLEEP; }
    }
}
