package net.onixary.shapeShifterCurseFabric.recipes;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.IsMorphScaleItemCondition;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.items.tools.SuperMorphScaleCore;

import static net.onixary.shapeShifterCurseFabric.recipes.RecipeSerializerRegister.MORPH_SCALE_UPGRADE;

public class MorphScaleUpgradeRecipe extends UpgradeRecipe {
    @Override
    public net.minecraft.recipe.RecipeType<?> getType() {
        return net.minecraft.recipe.RecipeType.SMITHING;
    }
    public final Ingredient template;
    public final Ingredient addition;

    public boolean isUpgradeAll() {
        return ShapeShifterCurseFabric.commonConfig.enableFullStackUpgrade;
    }

    public MorphScaleUpgradeRecipe(Identifier id, Ingredient template, Ingredient addition) {
        super(id, template, (itemStack -> {
            if (itemStack.isEmpty()) {
                return false;
            }
            NbtCompound nbtCompound = itemStack.getNbt();
            if (nbtCompound == null) {
                return true;
            }
            return !(nbtCompound.contains(IsMorphScaleItemCondition.IsMorphScaleArmorTagName) && nbtCompound.getBoolean(IsMorphScaleItemCondition.IsMorphScaleArmorTagName));
        }), addition, itemStack -> {
            NbtCompound nbtCompound = itemStack.getOrCreateNbt();
            nbtCompound.putBoolean(IsMorphScaleItemCondition.IsMorphScaleArmorTagName, true);
            return itemStack;
        });
        this.template = template;
        this.addition = addition;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        ItemStack coreStack = inventory.getStack(0);
        if (coreStack.isOf(RegCustomItem.SUPER_MORPHSCALE_CORE)) {
            ItemStack itemStack = inventory.getStack(1);
            int multiplier = SuperMorphScaleCore.getUpgradeDamageMultiplier(itemStack);
            int canCraftCount = SuperMorphScaleCore.getMaxUseCount(coreStack, multiplier);
            if (this.base.test(itemStack) && canCraftCount > 0) {
                ItemStack outputStack = itemStack.copy();
                outputStack.setCount(1);
                return this.upgradeResult.apply(outputStack);
            }
            return ItemStack.EMPTY;
        }
        return super.craft(inventory, registryManager);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MORPH_SCALE_UPGRADE;
    }

    @Override
    public boolean overrideVanillaOnTakeOutput() {
        return true;
    }

    @Override
    public void onTakeOutput(SmithingScreenHandler screenHandler, PlayerEntity player, ItemStack stack) {
        ItemStack coreStack = screenHandler.input.getStack(0);
        if (coreStack.isOf(RegCustomItem.SUPER_MORPHSCALE_CORE)) {
            ItemStack baseStack = screenHandler.input.getStack(1);
            int multiplier = SuperMorphScaleCore.getUpgradeDamageMultiplier(baseStack);
            SuperMorphScaleCore.damageItemAfterUpgrade(coreStack, multiplier);
            screenHandler.decrementStack(1);
            screenHandler.decrementStack(2);
        }
        else {
            screenHandler.decrementStack(0);
            if (this.isUpgradeAll()) {
                screenHandler.input.setStack(1, ItemStack.EMPTY);
            } else {
                screenHandler.decrementStack(1);
            }
            screenHandler.decrementStack(2);
        }
    }

    public static class Serializer implements RecipeSerializer<MorphScaleUpgradeRecipe> {

        private static final MapCodec<MorphScaleUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("template").forGetter(r -> r.template),
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(r -> r.addition)
        ).apply(instance, (template, addition) -> new MorphScaleUpgradeRecipe(Identifier.of("morph_scale_upgrade"), template, addition)));

        private static final PacketCodec<RegistryByteBuf, MorphScaleUpgradeRecipe> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC, r -> r.template,
            Ingredient.PACKET_CODEC, r -> r.addition,
            (template, addition) -> new MorphScaleUpgradeRecipe(Identifier.of("morph_scale_upgrade"), template, addition)
        );

        @Override
        public MapCodec<MorphScaleUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MorphScaleUpgradeRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
