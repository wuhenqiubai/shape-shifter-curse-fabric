package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusPotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TransformAction {

    public static class TransformToFormAction extends EntityActionType {
        public static final TypedDataObjectFactory<TransformToFormAction> DATA_FACTORY = TypedDataObjectFactory.simple(
                new SerializableData()
                        .add("form_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("instant", SerializableDataTypes.BOOLEAN, false),
                data -> new TransformToFormAction(data.getId("form_id"), data.getBoolean("instant")),
                (action, serializableData) -> serializableData.instance()
        );

        private final Identifier formId;
        private final boolean instant;

        public TransformToFormAction(Identifier formId, boolean instant) {
            this.formId = formId;
            this.instant = instant;
        }

        @Override
        public void accept(EntityActionContext context) {
            if (!(context.entity() instanceof PlayerEntity player)) return;
            if (formId == null) {
                ShapeShifterCurseFabric.LOGGER.warn("Missing form_id for TransformAction");
                return;
            }
            if (!RegPlayerForms.playerForms.containsKey(formId)) {
                ShapeShifterCurseFabric.LOGGER.warn("Invalid form_id for TransformAction: {}", formId);
                return;
            }
            PlayerFormBase pfb = RegPlayerForms.getPlayerForm(formId);
            if (instant) {
                TransformManager.setFormDirectly(player, pfb);
            } else {
                TransformManager.handleDirectTransform(player, pfb, false);
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return AdditionalEntityActions.TRANSFORM_TO_FORM;
        }
    }

    public static class GiveCustomTransformEffectAction extends EntityActionType {
        public static final TypedDataObjectFactory<GiveCustomTransformEffectAction> DATA_FACTORY = TypedDataObjectFactory.simple(
                new SerializableData()
                        .add("form_id", SerializableDataTypes.IDENTIFIER, null),
                data -> new GiveCustomTransformEffectAction(data.getId("form_id")),
                (action, serializableData) -> serializableData.instance()
        );

        private final Identifier formId;

        public GiveCustomTransformEffectAction(Identifier formId) {
            this.formId = formId;
        }

        @Override
        public void accept(EntityActionContext context) {
            if (context.entity() instanceof PlayerEntity player && formId != null) {
                CTPUtils.setTransformativePotionForm(player, formId);
                RegTStatusPotionEffect.TO_CUSTOM_STATUE_POTION.applyInstantEffect(player, player, player, 0, 1.0d);
            }
        }

        @Override
        public @NotNull ActionConfiguration<?> getConfig() {
            return AdditionalEntityActions.GIVE_CUSTOM_TRANSFORM_EFFECT;
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerAction(Consumer<ActionConfiguration<? extends EntityActionType>> actionReg,
                                       Consumer<ActionConfiguration<? extends BiEntityActionType>> biActionReg) {
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("transform_to_form"),
                TransformToFormAction.DATA_FACTORY
        ));
        actionReg.accept(ActionConfiguration.of(
                ShapeShifterCurseFabric.identifier("give_custom_transform_effect"),
                GiveCustomTransformEffectAction.DATA_FACTORY
        ));
    }
}