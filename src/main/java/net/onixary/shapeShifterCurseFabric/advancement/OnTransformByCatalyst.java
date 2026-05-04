package net.onixary.shapeShifterCurseFabric.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.function.Predicate;

public class OnTransformByCatalyst extends AbstractCriterion<OnTransformByCatalyst.Conditions> {
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Predicate<Conditions> predicate) {
        super.trigger(player, predicate);
    }

    public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = LootContextPredicate.CODEC
                .optionalFieldOf("player")
                .xmap(Conditions::new, Conditions::player)
                .codec();

        @Override
        public void validate(net.minecraft.predicate.entity.LootContextPredicateValidator validator) {
        }
    }
}