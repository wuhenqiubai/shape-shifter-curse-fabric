package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils.getPowerFoodComponent;

@Mixin(value = Item.class)
public abstract class CustomEdibleItemMixin {

    // 替换原 use$isFood（@ModifyExpressionValue → @Redirect）
    @Redirect(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z")
    )
    private boolean redirectUseIsFood(Item instance, World world, PlayerEntity user, Hand hand) {
        boolean original = instance.isFood();
        return getPowerFoodComponent(user, user.getStackInHand(hand)) != null || original;
    }

    // 替换原 use$getFoodComponent（@ModifyExpressionValue → @Redirect）
    @Redirect(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;")
    )
    private FoodComponent redirectUseGetFoodComponent(Item instance, World world, PlayerEntity user, Hand hand) {
        FoodComponent original = instance.getFoodComponent();
        FoodComponent fc = getPowerFoodComponent(user, user.getStackInHand(hand));
        return fc != null ? fc : original;
    }

    // 替换原 finishUsing$isFood（@ModifyExpressionValue → @Redirect）
    @Redirect(
            method = "finishUsing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z")
    )
    private boolean redirectFinishUsingIsFood(Item instance, ItemStack stack, World world, LivingEntity user) {
        boolean original = instance.isFood();
        if (user instanceof PlayerEntity playerEntity) {
            return getPowerFoodComponent(playerEntity, stack) != null || original;
        }
        return original;
    }
}