package net.onixary.shapeShifterCurseFabric.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "apply", at = @At("HEAD"))
    private void injectCustomRecipes(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        int loaded = 0;
        Map<Identifier, Resource> resources = resourceManager.findResources("recipes",
                id -> id.getNamespace().equals("shape-shifter-curse") && id.getPath().endsWith(".json"));
        for (var entry : resources.entrySet()) {
            Identifier fileId = entry.getKey();
            String path = fileId.getPath();
            // Strip "recipes/" prefix and ".json" suffix to get recipe ID
            String recipePath = path.substring("recipes/".length(), path.length() - ".json".length());
            Identifier recipeId = Identifier.of(fileId.getNamespace(), recipePath);
            if (map.containsKey(recipeId)) {
                continue;
            }
            try (var reader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {
                JsonElement json = JsonParser.parseReader(reader);
                if (json.isJsonObject()) {
                    map.put(recipeId, json);
                    loaded++;
                }
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("[SSC] Failed to read recipe {}", fileId, e);
            }
        }
        if (loaded > 0) {
            ShapeShifterCurseFabric.LOGGER.info("[SSC] Injected {} custom recipes into recipe manager", loaded);
        }
    }
}
