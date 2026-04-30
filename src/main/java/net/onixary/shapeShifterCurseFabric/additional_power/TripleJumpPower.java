package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TripleJumpPower extends PowerType {

    private final EntityAction firstJumpAction;
    private final EntityAction secondJumpAction;
    private final EntityAction thirdJumpAction;
    private final float firstJumpMultiplier;
    private final float secondJumpMultiplier;
    private final float thirdJumpMultiplier;
    private final int resetTicksOnGround;

    private int jumpCount = 0;
    private int ticksOnGround = 0;
    private float activeMultiplier = 1.0f;

    public static final TypedDataObjectFactory<TripleJumpPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("first_jump_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("second_jump_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("third_jump_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("first_jump_multiplier", SerializableDataTypes.FLOAT, 1.0f)
                            .add("second_jump_multiplier", SerializableDataTypes.FLOAT, 1.5f)
                            .add("third_jump_multiplier", SerializableDataTypes.FLOAT, 2.0f)
                            .add("reset_ticks_on_ground", SerializableDataTypes.INT, 10),
                    (data, condition) -> new TripleJumpPower(
                            data.get("first_jump_action"),
                            data.get("second_jump_action"),
                            data.get("third_jump_action"),
                            data.getFloat("first_jump_multiplier"),
                            data.getFloat("second_jump_multiplier"),
                            data.getFloat("third_jump_multiplier"),
                            data.getInt("reset_ticks_on_ground"),
                            condition),
                    (power, sd) -> sd.instance()
                            .set("first_jump_action", power.firstJumpAction)
                            .set("second_jump_action", power.secondJumpAction)
                            .set("third_jump_action", power.thirdJumpAction)
                            .set("first_jump_multiplier", power.firstJumpMultiplier)
                            .set("second_jump_multiplier", power.secondJumpMultiplier)
                            .set("third_jump_multiplier", power.thirdJumpMultiplier)
                            .set("reset_ticks_on_ground", power.resetTicksOnGround)
            );

    public TripleJumpPower(EntityAction firstJumpAction, EntityAction secondJumpAction, EntityAction thirdJumpAction,
                           float firstJumpMultiplier, float secondJumpMultiplier, float thirdJumpMultiplier,
                           int resetTicksOnGround, Optional<EntityCondition> condition) {
        super(condition);
        this.firstJumpAction = firstJumpAction;
        this.secondJumpAction = secondJumpAction;
        this.thirdJumpAction = thirdJumpAction;
        this.firstJumpMultiplier = firstJumpMultiplier;
        this.secondJumpMultiplier = secondJumpMultiplier;
        this.thirdJumpMultiplier = thirdJumpMultiplier;
        this.resetTicksOnGround = resetTicksOnGround;
    }

    @Override
    public void onGained() {
        this.setTicking();
    }

    @Override
    public void tick() {
        LivingEntity entity = getHolder();
        boolean shouldReset = false;
        if (!entity.isSprinting()) {
            shouldReset = true;
        }
        if (entity.isOnGround()) {
            ticksOnGround++;
            if (ticksOnGround > resetTicksOnGround) {
                shouldReset = true;
            }
        } else {
            ticksOnGround = 0;
        }
        if (shouldReset) {
            jumpCount = 0;
        }
    }

    public void onJump() {
        LivingEntity entity = getHolder();
        if (!isActive() || !entity.isOnGround() || !entity.isSprinting()) {
            this.activeMultiplier = 1.0f;
            return;
        }

        ticksOnGround = 0;
        jumpCount++;

        EntityAction action = null;

        switch (jumpCount) {
            case 1:
                this.activeMultiplier = this.firstJumpMultiplier;
                action = this.firstJumpAction;
                break;
            case 2:
                this.activeMultiplier = this.secondJumpMultiplier;
                action = this.secondJumpAction;
                break;
            case 3:
                this.activeMultiplier = this.thirdJumpMultiplier;
                action = this.thirdJumpAction;
                jumpCount = 0;
                break;
            default:
                jumpCount = 0;
                this.activeMultiplier = 1.0f;
                break;
        }

        if (action != null) {
            action.accept(entity);
        }
    }

    public float getActiveJumpMultiplier() {
        return this.activeMultiplier;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("triple_jump"));
    }

    public static PowerConfiguration<TripleJumpPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}