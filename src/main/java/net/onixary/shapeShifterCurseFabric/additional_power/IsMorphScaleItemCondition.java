package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.apoli.condition.type.ItemConditionType;
import io.github.apace100.apoli.condition.context.ItemConditionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ModTags;
import org.jetbrains.annotations.NotNull;

public class IsMorphScaleItemCondition extends ItemConditionType {

    public static final TypedDataObjectFactory<IsMorphScaleItemCondition> DATA_FACTORY =
            TypedDataObjectFactory.simple(
                    new SerializableData(),
                    data -> new IsMorphScaleItemCondition(),
                    (c, sd) -> sd.instance()
            );

    @Override
    public boolean test(ItemConditionContext ctx) {
        ItemStack itemStack = ctx.stack();
        if (itemStack.isIn(ModTags.MorphScaleItem_Tag)) return true;
        NbtComponent customData = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        return customData != null && customData.copyNbt().getBoolean(AdditionalItemCondition.IS_MORPH_SCALE_ARMOR_TAG);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return AdditionalItemCondition.IS_MORPH_SCALE_ITEM_CONFIG;
    }
}
