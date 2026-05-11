package net.onixary.shapeShifterCurseFabric.integration.origins.registry;

import io.github.apace100.calio.Calio;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Ensures origins namespace tags are available in Calio's REGISTRY_TAGS
 * before power data is loaded, preventing parse-time tag validation failures.
 */
public class OriginsTagLoader implements SimpleSynchronousResourceReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OriginsTagLoader.class);

    @Override
    public Identifier getFabricId() {
        return Origins.identifier("origins_tags");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<TagKey<?>, Collection<RegistryEntry<?>>> registryTags = Calio.REGISTRY_TAGS.get();
        if (registryTags == null) {
            registryTags = new HashMap<>();
            Calio.REGISTRY_TAGS.set(registryTags);
        }

        int count = 0;

        // Scan ALL namespaces for tag files to ensure they're available at parse time
        count += registerTags(registryTags, RegistryKeys.ITEM, manager, "items");
        count += registerTags(registryTags, RegistryKeys.BLOCK, manager, "blocks");
        count += registerTags(registryTags, RegistryKeys.ENTITY_TYPE, manager, "entity_type");
        count += registerTags(registryTags, RegistryKeys.DAMAGE_TYPE, manager, "damage_type");

        LOGGER.info("Registered {} missing tags across all namespaces", count);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int registerTags(Map<TagKey<?>, Collection<RegistryEntry<?>>> registryTags,
                             RegistryKey<? extends Registry<?>> registryKey,
                             ResourceManager manager,
                             String registryDir) {
        int count = 0;
        for (Identifier path : manager.findResources("tags/" + registryDir,
                id -> id.getPath().endsWith(".json"))
                .keySet()) {
            // path: origin/tags/items/ignore_diet.json
            String ns = path.getNamespace();
            String fileName = path.getPath().substring(path.getPath().lastIndexOf('/') + 1).replace(".json", "");
            TagKey<?> tagKey = TagKey.of((RegistryKey) registryKey, Identifier.of(ns, fileName));
            if (!registryTags.containsKey(tagKey)) {
                registryTags.put(tagKey, Collections.emptyList());
                count++;
            }
        }
        return count;
    }
}
