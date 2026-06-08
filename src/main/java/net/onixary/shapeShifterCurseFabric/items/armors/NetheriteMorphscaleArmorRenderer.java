package net.onixary.shapeShifterCurseFabric.items.armors;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class NetheriteMorphscaleArmorRenderer extends GeoArmorRenderer<NetheriteMorphScaleArmor> {
    public NetheriteMorphscaleArmorRenderer() {
        super(new DefaultedGeoModel<>(Identifier.of(ShapeShifterCurseFabric.MOD_ID, "netherite_morphscale_armor")) {
            @Override
            protected String subtype() {
                return "item";
            }
        });
    }
}
