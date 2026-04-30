package net.onixary.shapeShifterCurseFabric.integration.origins.power;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.component.OriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayer;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayers;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class OriginsEntityConditions {

    public static void register() {
        Registry.register(ApoliRegistries.ENTITY_CONDITION_TYPE,
                Origins.identifier("origin"),
                ConditionConfiguration.of(Origins.identifier("origin"), OriginCondition.DATA_FACTORY));
    }

    public static class OriginCondition extends EntityConditionType {

        public static final TypedDataObjectFactory<OriginCondition> DATA_FACTORY =
                TypedDataObjectFactory.simple(
                        new SerializableData()
                                .add("origin", SerializableDataTypes.IDENTIFIER)
                                .add("layer", SerializableDataTypes.IDENTIFIER, null),
                        data -> new OriginCondition(data.getId("origin"), data.getId("layer")),
                        (c, sd) -> sd.instance()
                );

        private final Identifier originId;
        private final Identifier layerId;

        public OriginCondition(Identifier originId, Identifier layerId) {
            this.originId = originId;
            this.layerId = layerId;
        }

        @Override
        public boolean test(EntityConditionContext ctx) {
            if (!(ctx.entity() instanceof PlayerEntity)) return false;
            OriginComponent component = ModComponents.ORIGIN.get(ctx.entity());
            if (layerId != null) {
                OriginLayer layer = OriginLayers.getLayer(layerId);
                if (layer == null) return false;
                Origin origin = component.getOrigin(layer);
                return origin != null && origin.getIdentifier().equals(originId);
            }
            return component.getOrigins().values().stream()
                    .anyMatch(o -> o.getIdentifier().equals(originId));
        }

        @Override
        public @NotNull ConditionConfiguration<?> getConfig() {
            return ConditionConfiguration.of(Origins.identifier("origin"), DATA_FACTORY);
        }
    }
}
