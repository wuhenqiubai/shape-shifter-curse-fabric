package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

public class DiggingBareHandCondition extends EntityConditionType {

    public static final TypedDataObjectFactory<DiggingBareHandCondition> DATA_FACTORY =
            TypedDataObjectFactory.simple(
                    new SerializableData(),
                    data -> new DiggingBareHandCondition(),
                    (c, sd) -> sd.instance()
            );

    @Override
    public boolean test(EntityConditionContext ctx) {
        if (!(ctx.entity() instanceof PlayerEntity player)) return false;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (!serverPlayer.interactionManager.mining) return false;
        } else if (player instanceof ClientPlayerEntity) {
            ClientPlayerInteractionManager im = MinecraftClient.getInstance().interactionManager;
            if (im == null || !im.isBreakingBlock()) return false;
        } else {
            return false;
        }

        if (player.getInventory().getMainHandStack().isEmpty()) return true;
        if (player.getInventory().getMainHandStack().getItem() instanceof ToolItem tool)
            return tool.getMaterial().getMiningLevel() <= 0;
        return true;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ConditionConfiguration.of(ShapeShifterCurseFabric.identifier("barehand_digging"), DATA_FACTORY);
    }
}