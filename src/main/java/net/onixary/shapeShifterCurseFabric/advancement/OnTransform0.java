package net.onixary.shapeShifterCurseFabric.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Optional;

public class OnTransform0 extends AbstractCriterion<OnTransform0.Condition> {
    public static final Identifier ID = Identifier.of(ShapeShifterCurseFabric.MOD_ID, "on_transform_0");

    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, Condition::requirementsMet);
    }

    @Override
    public Codec<Condition> getConditionsCodec() {
        return Condition.CODEC;
    }

    public record Condition(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                LootContextPredicate.CODEC.optionalFieldOf("player").forGetter(Condition::player)
            ).apply(instance, Condition::new)
        );

        public boolean requirementsMet() {
            return true;
        }
    }
}
