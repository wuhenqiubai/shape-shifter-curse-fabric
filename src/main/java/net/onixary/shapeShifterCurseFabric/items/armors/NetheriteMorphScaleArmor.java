package net.onixary.shapeShifterCurseFabric.items.armors;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.List;
import java.util.function.Consumer;

public class NetheriteMorphScaleArmor extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public NetheriteMorphScaleArmor(Type type) {
        super(NetheriteMorphscaleArmorMaterial.ENTRY, type, new Settings().maxCount(1).fireproof()
                    .maxDamage(type.getMaxDamage(26)));
        GeoItem.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private NetheriteMorphscaleArmorRenderer renderer;

            @SuppressWarnings("unchecked")
            @Override
            public <T extends LivingEntity> BipedEntityModel<?> getGeoArmorRenderer(T livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<T> original) {
                if (this.renderer == null)
                    this.renderer = new NetheriteMorphscaleArmorRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.morphscale_armor.tooltip").formatted(Formatting.YELLOW));
    }
}
