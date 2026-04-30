package net.onixary.shapeShifterCurseFabric.integration.origins.power;

import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.PowerReference;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.minecraft.registry.Registry;

@SuppressWarnings("unchecked")
public class OriginsPowerTypes {

    public static final PowerReference LIKE_WATER = PowerReference.of(Origins.identifier("like_water"));
    public static final PowerReference WATER_BREATHING = PowerReference.of(Origins.identifier("water_breathing"));
    public static final PowerReference SCARE_CREEPERS = PowerReference.of(Origins.identifier("scare_creepers"));
    public static final PowerReference WATER_VISION = PowerReference.of(Origins.identifier("water_vision"));
    public static final PowerReference NO_COBWEB_SLOWDOWN = PowerReference.of(Origins.identifier("no_cobweb_slowdown"));
    public static final PowerReference MASTER_OF_WEBS_NO_SLOWDOWN = PowerReference.of(Origins.identifier("master_of_webs_no_slowdown"));
    public static final PowerReference CONDUIT_POWER_ON_LAND = PowerReference.of(Origins.identifier("conduit_power_on_land"));

    public static final PowerConfiguration<OriginsCallbackPower> ACTION_ON_CALLBACK =
            register(PowerConfiguration.of(Origins.identifier("action_on_callback"), OriginsCallbackPower.DATA_FACTORY));

    public static void register() {}

    private static <T extends io.github.apace100.apoli.power.type.PowerType> PowerConfiguration<T> register(PowerConfiguration<T> config) {
        Registry.register(ApoliRegistries.POWER_TYPE, config.id(), config);
        return config;
    }
}
