package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ModTags;

public class IsMorphScaleItemCondition {
    public static final String IsMorphScaleArmorTagName = "MorphScaleItem";

    public static boolean condition(SerializableData.Instance data, Pair<World, ItemStack> worldAndStack) {
        ItemStack itemStack = worldAndStack.getRight();
        if (itemStack.isIn(ModTags.MorphScaleItem_Tag)) {
            return true;
        }
        var customData = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            if (customData.copyNbt().getBoolean(IsMorphScaleArmorTagName)) {
                return true;
            }
        }
        return false;
    }

    public static ConditionFactory<Pair<World, ItemStack>> getFactory() {
	    return new ConditionFactory<>(
			    ShapeShifterCurseFabric.identifier("is_morph_scale_item"),
			    new SerializableData(),
			    IsMorphScaleItemCondition::condition
        );
    }
}
