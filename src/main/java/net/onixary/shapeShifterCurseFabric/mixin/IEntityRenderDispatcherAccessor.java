package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderDispatcher.class)
public interface IEntityRenderDispatcherAccessor {
    @Accessor("itemRenderer")
    ItemRenderer getItemRenderer();
}
