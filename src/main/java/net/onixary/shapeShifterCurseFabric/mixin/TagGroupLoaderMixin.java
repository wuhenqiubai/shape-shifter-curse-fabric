package net.onixary.shapeShifterCurseFabric.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mixin.accessor.TagGroupLoaderAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(TagGroupLoader.class)
public class TagGroupLoaderMixin {
    @Inject(method = "loadTags", at = @At("RETURN"), cancellable = false)
    private void injectCustomTags(ResourceManager resourceManager, CallbackInfoReturnable<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> cir) {
        String dataType = ((TagGroupLoaderAccessor) this).getDataType();
        // dataType is like "tags/items", "tags/blocks", "tags/entity_types" etc.
        Map<Identifier, List<TagGroupLoader.TrackedEntry>> map = cir.getReturnValue();
        Map<Identifier, Resource> resources = resourceManager.findResources(dataType,
                id -> id.getNamespace().equals("shape-shifter-curse") && id.getPath().endsWith(".json"));
        int loaded = 0;
        for (var entry : resources.entrySet()) {
            Identifier fileId = entry.getKey();
            String path = fileId.getPath();
            // Strip "tags/<type>/" prefix and ".json" suffix
            String tagPath = path.substring(dataType.length() + 1, path.length() - ".json".length());
            Identifier tagId = Identifier.of(fileId.getNamespace(), tagPath);
            if (map.containsKey(tagId)) {
                continue;
            }
            try (var reader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {
                JsonElement json = JsonParser.parseReader(reader);
                TagFile tagFile = TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, json)).getOrThrow();
                List<TagGroupLoader.TrackedEntry> entries = new ArrayList<>();
                for (TagEntry tagEntry : tagFile.entries()) {
                    entries.add(new TagGroupLoader.TrackedEntry(tagEntry, fileId.getNamespace()));
                }
                map.put(tagId, entries);
                loaded++;
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("[SSC] Failed to read tag {}", fileId, e);
            }
        }
        if (loaded > 0) {
            ShapeShifterCurseFabric.LOGGER.info("[SSC] Injected {} custom tags (type: {})", loaded, dataType);
        }
    }
}
