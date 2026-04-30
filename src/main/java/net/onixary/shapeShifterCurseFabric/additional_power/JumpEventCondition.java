package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class JumpEventCondition extends EntityConditionType {

    private static final Set<PlayerEntity> jumpingPlayers = new HashSet<>();

    public static final TypedDataObjectFactory<JumpEventCondition> DATA_FACTORY =
            TypedDataObjectFactory.simple(
                    new SerializableData(),
                    data -> new JumpEventCondition(),
                    (c, sd) -> sd.instance()
            );

    @Override
    public boolean test(EntityConditionContext ctx) {
        return ctx.entity() instanceof PlayerEntity p && jumpingPlayers.contains(p);
    }

    public static void setJumping(PlayerEntity player, boolean jumping) {
        if (jumping) jumpingPlayers.add(player);
        else jumpingPlayers.remove(player);
    }

    public static void clearJumpState(PlayerEntity player) {
        jumpingPlayers.remove(player);
    }

    public static void tick() {
        jumpingPlayers.clear();
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("jump_event"), DATA_FACTORY);
    }
}