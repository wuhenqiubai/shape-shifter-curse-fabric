package net.onixary.shapeShifterCurseFabric.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.recipes.RecipeSerializerRegister.MORPH_SCALE_UPGRADE;

public class MorphScaleUpgradeRecipe extends UpgradeRecipe {
    public final Ingredient template;
    public final Ingredient addition;

    public MorphScaleUpgradeRecipe(Identifier id, Ingredient template, Ingredient addition) {
        super(id,
            template,
            itemStack -> {
                if (itemStack.isEmpty()) return false;
                NbtComponent nbt = itemStack.get(DataComponentTypes.CUSTOM_DATA);
                if (nbt == null) return true;
                NbtCompound nbtCompound = nbt.copyNbt();
                return !(nbtCompound.contains("MorphScaleItem") && nbtCompound.getBoolean("MorphScaleItem"));
            },
            addition,
            itemStack -> {
                NbtCompound nbtCompound = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                nbtCompound.putBoolean("MorphScaleItem", true);
                itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCompound));
                return itemStack;
            });
        this.template = template;
        this.addition = addition;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MORPH_SCALE_UPGRADE;
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
