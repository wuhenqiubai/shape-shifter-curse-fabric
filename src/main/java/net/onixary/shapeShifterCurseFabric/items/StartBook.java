package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;

public class StartBook extends Item {
    public StartBook(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ShapeShifterCurseFabricClient.openStartBookScreen(user);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
