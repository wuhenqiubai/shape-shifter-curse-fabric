package net.onixary.shapeShifterCurseFabric.integration.origins.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ChoseOriginCriterion extends AbstractCriterion<ChoseOriginCriterion.Conditions> {

    public static ChoseOriginCriterion INSTANCE = new ChoseOriginCriterion();

    private static final Identifier ID = Identifier.of(Origins.MODID, "chose_origin");

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Origin origin) {
        this.trigger(player, (conditions -> conditions.matches(origin)));
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public record Conditions(Optional<LootContextPredicate> player, Identifier originId) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        LootContextPredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                        Identifier.CODEC.fieldOf("origin").forGetter(Conditions::originId)
                ).apply(instance, Conditions::new)
        );

        public boolean matches(Origin origin) {
            return origin.getIdentifier().equals(originId);
        }

        @Override
        public void validate(net.minecraft.predicate.entity.LootContextPredicateValidator validator) {
        }
    }
}