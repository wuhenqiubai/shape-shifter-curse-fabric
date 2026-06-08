package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.SwimAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_Axolotl2 extends PlayerFormBase {
    public Form_Axolotl2(Identifier formID) {
        super(formID);
    }

    private static final AnimUtils.AnimationHolderData ANIM_CRAWL_IDLE = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_crawling_idle_new"));
    private static final AnimUtils.AnimationHolderData ANIM_CRAWL = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_crawling_new"));
    private static final AnimUtils.AnimationHolderData ANIM_CRAWL_JUMP = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_crawling_jump"));
    private static final AnimUtils.AnimationHolderData ANIM_CRAWL_ATTACK = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_crawling_attack_once"));
    private static final AnimUtils.AnimationHolderData ANIM_CRAWL_MINE = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_crawling_tool_swing"));
    static { ANIM_CRAWL_IDLE.skipFade = ANIM_CRAWL.skipFade = ANIM_CRAWL_JUMP.skipFade = ANIM_CRAWL_ATTACK.skipFade = ANIM_CRAWL_MINE.skipFade = true; }

    public static final AbstractAnimStateController SWIM_CONTROLLER = new SwimAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_swimming_idle")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("axolotl_2_swimming")));
    public static final AbstractAnimStateController IDLE_CONTROLLER = new WithSneakAnimController(null, ANIM_CRAWL_IDLE);
    public static final AbstractAnimStateController WALK_CONTROLLER = new WithSneakAnimController(null, ANIM_CRAWL);
    public static final AbstractAnimStateController JUMP_CONTROLLER = new WithSneakAnimController(null, ANIM_CRAWL_JUMP);
    public static final AbstractAnimStateController ATTACK_CONTROLLER = new WithSneakAnimController(null, ANIM_CRAWL_ATTACK);
    public static final AbstractAnimStateController MINING_CONTROLLER = new WithSneakAnimController(null, ANIM_CRAWL_MINE);

    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
	        return switch (animStateEnum) {
		        case ANIM_STATE_SWIM -> SWIM_CONTROLLER;
		        case ANIM_STATE_IDLE -> IDLE_CONTROLLER;
		        case ANIM_STATE_WALK -> WALK_CONTROLLER;
		        case ANIM_STATE_SPRINT -> WALK_CONTROLLER;
		        case ANIM_STATE_JUMP -> JUMP_CONTROLLER;
		        case ANIM_STATE_FALL -> IDLE_CONTROLLER;
		        case ANIM_STATE_ATTACK -> ATTACK_CONTROLLER;
		        case ANIM_STATE_MINING -> MINING_CONTROLLER;
		        default -> null;
	        };
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
