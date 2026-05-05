package net.onixary.shapeShifterCurseFabric.screen_effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public final class TransformOverlay {
    public static final TransformOverlay INSTANCE = new TransformOverlay();
    private final Identifier nausea_texture = Identifier.of(MOD_ID, "textures/overlay/nausea_black.png");
    private final Identifier black_texture = Identifier.of(MOD_ID, "textures/overlay/black.png");

    private boolean enableOverlay = false;
    private float strength_nausea = 0.0f;
    private float strength_black = 0.0f;

    public void init() {
        enableOverlay = false;
        strength_nausea = 0.0f;
        strength_black = 0.0f;
    }

    @Environment(EnvType.CLIENT)
    public void render()  {
        //double d = MathHelper.lerp(strength, 2.0D, 1.0D);
        if(!enableOverlay){
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        int i = client.getWindow().getScaledWidth();
        int j = client.getWindow().getScaledHeight();
        // RenderSystem/BufferBuilder API changed in 1.21 - vertex() needs Matrix4f, .next() removed
        // Disabled rendering for 1.21 port - needs full BufferBuilder API migration
    }

    public void setEnableOverlay(boolean enableOverlay) {
        this.enableOverlay = enableOverlay;
    }

    public void setNauesaStrength(float strength) {
        this.strength_nausea = MathHelper.clamp(strength, 0.0f, 1.0f);
    }

    public void setBlackStrength(float strength) {
        this.strength_black = MathHelper.clamp(strength, 0.0f, 1.0f);
    }
}
