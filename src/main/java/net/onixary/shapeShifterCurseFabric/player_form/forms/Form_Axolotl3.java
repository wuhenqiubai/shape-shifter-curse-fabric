package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.OneAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RushJumpAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.SwimAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_Axolotl3 extends PlayerFormBase {
    public Form_Axolotl3(Identifier formID) {
        super(formID);
    }

    // 爬行动画跳过 fade 过渡，避免 Euler 角插值绕大圈
    private static final AnimUtils.AnimationHolderData ANIM_CRAWL_IDLE = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_3_crawling_idle"));
    private static final AnimUtils.AnimationHolderData ANIM_CRAWL = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_3_crawling"));
    private static final AnimUtils.AnimationHolderData ANIM_CRAWL_JUMP = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_crawling_jump"));
    private static final AnimUtils.AnimationHolderData ANIM_CRAWL_ATTACK = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_crawling_attack_once"));
    private static final AnimUtils.AnimationHolderData ANIM_CRAWL_MINE = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_crawling_tool_swing"));
    static { ANIM_CRAWL_IDLE.skipFade = ANIM_CRAWL.skipFade = ANIM_CRAWL_JUMP.skipFade = ANIM_CRAWL_ATTACK.skipFade = ANIM_CRAWL_MINE.skipFade = true; }

    public static final AbstractAnimStateController SWIM_CONTROLLER = new SwimAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_swimming_idle")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_swimming")));
    public static final AbstractAnimStateController IDLE_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_3_idle")), ANIM_CRAWL_IDLE);
    public static final AbstractAnimStateController WALK_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_3_walk")), ANIM_CRAWL);
    public static final AbstractAnimStateController SPRINT_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_3_run")), ANIM_CRAWL);
    public static final AbstractAnimStateController JUMP_CONTROLLER = new RushJumpAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_3_jump")), ANIM_CRAWL_JUMP, new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_3_rush_jump"), 1.0f, 10), ANIM_CRAWL_JUMP);
    public static final AbstractAnimStateController FALL_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_3_jump")), ANIM_CRAWL_IDLE);
    public static final AbstractAnimStateController ATTACK_CONTROLLER = new WithSneakAnimController(null, ANIM_CRAWL_ATTACK);
    public static final AbstractAnimStateController MINING_CONTROLLER = new WithSneakAnimController(null, ANIM_CRAWL_MINE);
    public static final AbstractAnimStateController FLYING_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_3_creative_flight")));

    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_SWIM: return SWIM_CONTROLLER;
                case ANIM_STATE_IDLE: return IDLE_CONTROLLER;
                case ANIM_STATE_WALK: return WALK_CONTROLLER;
                case ANIM_STATE_SPRINT: return SPRINT_CONTROLLER;
                case ANIM_STATE_JUMP: return JUMP_CONTROLLER;
                case ANIM_STATE_FALL: return FALL_CONTROLLER;
                case ANIM_STATE_ATTACK: return ATTACK_CONTROLLER;
                case ANIM_STATE_MINING: return MINING_CONTROLLER;
                case ANIM_STATE_FLYING: return FLYING_CONTROLLER;
                default: return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
