package net.onixary.shapeShifterCurseFabric.integration;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;

import java.lang.reflect.Method;

/**
 * 通过反射调用 EMF Animation API，避免硬依赖 Entity Model Features。
 */
public class EMFIntegration {
    private static final boolean EMF_LOADED;
    private static Method emfEntityOf;
    private static Method pauseAllCustomAnimationsForEntity;
    private static Method resumeAllCustomAnimationsForEntity;

    static {
        boolean loaded = false;
        if (FabricLoader.getInstance().isModLoaded("entity_model_features")) {
            try {
                Class<?> api = Class.forName("traben.entity_model_features.EMFAnimationApi");
                emfEntityOf = api.getMethod("emfEntityOf", Entity.class);
                Class<?> emfEntityType = emfEntityOf.getReturnType();
                pauseAllCustomAnimationsForEntity = api.getMethod("pauseAllCustomAnimationsForEntity", emfEntityType);
                resumeAllCustomAnimationsForEntity = api.getMethod("resumeAllCustomAnimationsForEntity", emfEntityType);
                loaded = true;
            } catch (Exception ignored) {}
        }
        EMF_LOADED = loaded;
    }

    public static void pauseAnimations(Entity entity) {
        if (!EMF_LOADED) return;
        try {
            Object emfEntity = emfEntityOf.invoke(null, entity);
            pauseAllCustomAnimationsForEntity.invoke(null, emfEntity);
        } catch (Exception ignored) {}
    }

    public static void resumeAnimations(Entity entity) {
        if (!EMF_LOADED) return;
        try {
            Object emfEntity = emfEntityOf.invoke(null, entity);
            resumeAllCustomAnimationsForEntity.invoke(null, emfEntity);
        } catch (Exception ignored) {}
    }
}
