package net.onixary.shapeShifterCurseFabric.mixin.integration;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.helpers.FoodHelper;

@Mixin(FoodHelper.class)
// 排除逻辑在net.onixary.shapeShifterCurseFabric.mixin.plugin.MixinConfigPlugin里临时注册
public class AppleSkin {
    static {
        // 写一个理应不会抛异常的静态代码块，防止之后修改MixinConfigPlugin出错导致出现其他问题
        if (!FabricLoader.getInstance().isModLoaded("appleskin")) {
            throw new IllegalStateException("AppleSkin mixin was loaded but appleskin is not installed!");
        }
    }

    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private static void shapeShifterCurseFabric$canConsume(PlayerEntity player, FoodComponent foodComponent, CallbackInfoReturnable<Boolean> cir) {
        if (player != null && foodComponent != null) {
            boolean CanConsume = player.canConsume(foodComponent.canAlwaysEat());
            if (CanConsume) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getDefaultFoodValues", at = @At("HEAD"), cancellable = true)
    private static void shapeShifterCurseFabric$getDefaultFoodValues(ItemStack itemStack, CallbackInfoReturnable<FoodComponent> cir) {
        if (itemStack != null) {
            FoodComponent customFood = CustomEdibleUtils.getPowerFoodComponent(MinecraftClient.getInstance().player, itemStack);
            if (customFood != null) {
                cir.setReturnValue(customFood);
            }
        }
    }
}
