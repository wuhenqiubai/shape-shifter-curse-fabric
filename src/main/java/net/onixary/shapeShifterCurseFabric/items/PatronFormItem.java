package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PatronFormItem extends Item {
    public PatronFormItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!PatronUtils.EnablePatronFeature) {
            return super.use(world, user, hand);
        }
        if (!world.isClient) {
            ModPacketsS2CServer.OpenPatronFormSelectMenu(((ServerPlayerEntity) user));
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.of("This Feature Is In Development"));
    }
}
