package net.onixary.shapeShifterCurseFabric.player_form.transform;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormGroup;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;

import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.handleDirectTransform;

public class TransformRelatedItems {
    private TransformRelatedItems() {
    }

    public static final Item TRANSFORM_CURE = RegCustomItem.INHIBITOR;
    public static final Item TRANSFORM_CURE_FINAL = RegCustomItem.POWERFUL_INHIBITOR;
    public static final Item TRANSFORM_CURE_CREATIVE = RegCustomItem.CREATIVE_INHIBITOR;
    public static final Item TRANSFORM_CATALYST = RegCustomItem.CATALYST;
    public static final Item TRANSFORM_POWERFUL_CATALYST = RegCustomItem.POWERFUL_CATALYST;

    public static void OnUseCure(PlayerEntity player) {
        // 如果不是最终阶段，则回退一个阶段
        // If not the final stage, revert one stage
        PlayerFormBase currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        PlayerFormGroup currentFormGroup = currentForm.getGroup();
        PlayerFormBase toForm = null;
        switch (currentFormIndex) {
            case PlayerFormPhase.INDEX_PRE_ACTIVATE:
                // 无用
                // Useless
                break;
            case PlayerFormPhase.INDEX_BASE_SHIFTER:
                // 无用
                // Useless
                player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_cure").formatted(Formatting.YELLOW));
                break;
            case PlayerFormPhase.INDEX_PHASE_0:
                toForm = RegPlayerForms.ORIGINAL_SHIFTER;
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure_0").formatted(Formatting.YELLOW));
                // 触发自定义成就
                // Trigger custom achievement
                ShapeShifterCurseFabric.ON_TRANSFORM_BY_CURE.trigger((ServerPlayerEntity) player);
                break;
            case PlayerFormPhase.INDEX_PHASE_1:
                toForm = currentFormGroup.getForm(0);
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure").formatted(Formatting.YELLOW));
                // 触发自定义成就
                // Trigger custom achievement
                ShapeShifterCurseFabric.ON_TRANSFORM_BY_CURE.trigger((ServerPlayerEntity) player);
                break;
            case PlayerFormPhase.INDEX_PHASE_2:
                // 不会生效
                // Useless
                player.sendMessage(Text.translatable("info.shape-shifter-curse.max_form_used_cure").formatted(Formatting.YELLOW));
                break;
            case PlayerFormPhase.INDEX_PHASE_3:
                // 永久形态不会生效
                // Permanent form will not be affected
                player.sendMessage(Text.translatable("info.shape-shifter-curse.permanent_form_used_cure").formatted(Formatting.YELLOW));
                break;
            case PlayerFormPhase.INDEX_PHASE_SP:
                // SP form可以随时被治愈
                // SP form can be cured at any time
                toForm = RegPlayerForms.ORIGINAL_SHIFTER;
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure_0").formatted(Formatting.YELLOW));
                break;
            default:
                break;
        }
        if (toForm == null) {
            return;
        }

        handleDirectTransform(player, toForm, true);
    }

    public static void OnUseCureFinal(PlayerEntity player) {
        // 可以回退到最初阶段
        PlayerFormBase currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        PlayerFormGroup currentFormGroup = currentForm.getGroup();
        PlayerFormBase toForm = null;
        //遇到了魔法数字,意义不明，不敢动，如果重构建议使用 enum+if-else if或者新的switch
        switch (currentFormIndex) {
            case PlayerFormPhase.INDEX_PRE_ACTIVATE:
                // 无用
                // Useless
                break;
            case PlayerFormPhase.INDEX_BASE_SHIFTER:
                // 无用
                // Useless
                player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_cure_final").formatted(Formatting.YELLOW));
                break;
            case PlayerFormPhase.INDEX_PHASE_0:
                toForm = RegPlayerForms.ORIGINAL_SHIFTER;
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure_final").formatted(Formatting.YELLOW));
                // 触发自定义成就
                // Trigger custom achievement
                ShapeShifterCurseFabric.ON_TRANSFORM_BY_CURE.trigger((ServerPlayerEntity) player);
                break;
            case PlayerFormPhase.INDEX_PHASE_1:
                toForm = RegPlayerForms.ORIGINAL_SHIFTER;
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure_final").formatted(Formatting.YELLOW));
                // 触发自定义成就
                // Trigger custom achievement
                ShapeShifterCurseFabric.ON_TRANSFORM_BY_CURE.trigger((ServerPlayerEntity) player);
                break;
            case PlayerFormPhase.INDEX_PHASE_2:
                toForm = currentFormGroup.getForm(1);
                player.sendMessage(Text.translatable("info.shape-shifter-curse.max_form_used_cure_final").formatted(Formatting.YELLOW));
                // 触发自定义成就
                // Trigger custom achievement
                ShapeShifterCurseFabric.ON_TRANSFORM_BY_CURE_FINAL.trigger((ServerPlayerEntity) player);
                ShapeShifterCurseFabric.ON_TRANSFORM_BY_CURE.trigger((ServerPlayerEntity) player);
                break;
            case PlayerFormPhase.INDEX_PHASE_3:
                // 永久形态不会生效
                // Permanent form will not be affected
                player.sendMessage(Text.translatable("info.shape-shifter-curse.permanent_form_used_cure_final").formatted(Formatting.YELLOW));
                break;
            case PlayerFormPhase.INDEX_PHASE_SP:
                // SP form可以随时被治愈
                // SP form can be cured at any time
                toForm = RegPlayerForms.ORIGINAL_SHIFTER;
                player.sendMessage(Text.translatable("info.shape-shifter-curse.transformed_by_cure_0").formatted(Formatting.YELLOW));
                break;
            default:
                break;
        }
        if (toForm == null) {
            return;
        }

        handleDirectTransform(player, toForm, true);
    }

    public static void OnUseCreativeCure(PlayerEntity player){
        // 创造模式下才能获取的抑制剂，可以将永久形态回退到最初阶段
        // The inhibitor that can only be obtained in creative mode can revert the permanent form to the original stage
        PlayerFormBase currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        if(currentForm != RegPlayerForms.ORIGINAL_SHIFTER && currentForm != RegPlayerForms.ORIGINAL_BEFORE_ENABLE){
            handleDirectTransform(player, RegPlayerForms.ORIGINAL_SHIFTER, true);
        }
    }

    public static void OnUseCatalyst(ServerPlayerEntity player) {
        // 在origin power中处理instinct相关逻辑，这里只显示提示与特殊逻辑
        // Instinct-related logic is handled in origin power, here only shows prompt and special logic
        PlayerFormBase currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        PlayerFormGroup currentFormGroup = currentForm.getGroup();
        PlayerFormBase toForm = null;
        switch (currentFormIndex) {
            case PlayerFormPhase.INDEX_PRE_ACTIVATE:
                break;
            case PlayerFormPhase.INDEX_BASE_SHIFTER:
                // 特殊逻辑：查看当前是否有在生效的效果，有的话则应用，没有的话则无用
                // Special logic: check if there is an active effect, if so, apply it, otherwise useless
                if (EffectManager.hasTransformativeEffect(player)) {
                    EffectManager.ActiveTransformativeEffect(player);
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_catalyst_attached").formatted(Formatting.YELLOW));
                    ShapeShifterCurseFabric.ON_TRANSFORM_BY_CATALYST.trigger((ServerPlayerEntity) player);
                }
                else{
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.origin_form_used_catalyst").formatted(Formatting.YELLOW));
                }
                break;
            case PlayerFormPhase.INDEX_PHASE_0:
                //toForm = PlayerForms.getFormsByGroup(currentFormGroup)[1];
                if(currentForm.getIgnoreCatalyst()){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.use_catalyst_when_ignore").formatted(Formatting.DARK_PURPLE));
                }
                else{
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.use_catalyst").formatted(Formatting.YELLOW));
                }
                break;
            case PlayerFormPhase.INDEX_PHASE_1:
                //toForm = PlayerForms.getFormsByGroup(currentFormGroup)[2];
                if(currentForm.getIgnoreCatalyst()){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.use_catalyst_when_ignore").formatted(Formatting.DARK_PURPLE));
                }
                else{
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.use_catalyst").formatted(Formatting.YELLOW));
                }
                break;
            case PlayerFormPhase.INDEX_PHASE_2:
                if(currentForm.getIgnoreCatalyst()){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.use_catalyst_when_ignore").formatted(Formatting.DARK_PURPLE));
                }
                else{
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.max_form_used_catalyst").formatted(Formatting.YELLOW));
                }

                break;
            case PlayerFormPhase.INDEX_PHASE_SP:
                player.sendMessage(Text.translatable("info.shape-shifter-curse.sp_form_used_catalyst").formatted(Formatting.YELLOW));
                break;
            default:
                break;
        }
        if (toForm == null) {
            return;
        }
        handleDirectTransform(player, toForm, false);
    }

    public static void OnUsePowerfulCatalyst(PlayerEntity player) {
        // 在origin power中处理instinct相关逻辑，这里只显示提示与特殊逻辑
        // Instinct-related logic is handled in origin power, here only shows hint text and special logic
        PlayerFormBase currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        PlayerFormGroup currentFormGroup = currentForm.getGroup();
        PlayerFormBase toForm = null;
        //遇到了魔法数字,意义不明，不敢动，如果重构建议使用 enum+if-else if或者新的switch
        switch (currentFormIndex) {
            case PlayerFormPhase.INDEX_PRE_ACTIVATE:
                // 无用
                break;
            case PlayerFormPhase.INDEX_BASE_SHIFTER:
            case PlayerFormPhase.INDEX_PHASE_0:
            case PlayerFormPhase.INDEX_PHASE_1:
                player.sendMessage(Text.translatable("info.shape-shifter-curse.form_used_powerful_catalyst_failed").formatted(Formatting.YELLOW));
                break;
            case PlayerFormPhase.INDEX_PHASE_2:
                if (currentFormGroup.hasForm(3)) {
                    toForm = currentFormGroup.getForm(3);
                }
                if(toForm != null){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.max_form_used_powerful_catalyst").formatted(Formatting.YELLOW));
                }
                else{
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.form_used_powerful_catalyst_failed").formatted(Formatting.YELLOW));
                }
                break;
            case PlayerFormPhase.INDEX_PHASE_SP:
                player.sendMessage(Text.translatable("info.shape-shifter-curse.form_used_powerful_catalyst_failed").formatted(Formatting.YELLOW));
                break;
            default:
                break;
        }
        if (toForm == null) {
            return;
        }
        handleDirectTransform(player, toForm, false);
    }
}
