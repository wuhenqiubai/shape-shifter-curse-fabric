package net.onixary.shapeShifterCurseFabric.mixin.accessor;

import net.minecraft.registry.tag.TagGroupLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagGroupLoader.class)
public interface TagGroupLoaderAccessor {
    @Accessor("dataType")
    String getDataType();
}
