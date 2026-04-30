package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class FireArrowAction extends EntityActionType {

    public static final TypedDataObjectFactory<FireArrowAction> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("damage", SerializableDataTypes.FLOAT, 2.0f)
                    .add("speed", SerializableDataTypes.FLOAT, 3.0f)
                    .add("spread", SerializableDataTypes.FLOAT, 0.0f)
                    .add("fire_time", SerializableDataTypes.INT, 0)
                    .add("no_gravity", SerializableDataTypes.BOOLEAN, false)
                    .add("critical", SerializableDataTypes.BOOLEAN, false)
                    .add("has_owner", SerializableDataTypes.BOOLEAN, true)
                    .add("projectile_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add("count", SerializableDataTypes.INT, 1),
            data -> new FireArrowAction(
                    data.getFloat("damage"),
                    data.getFloat("speed"),
                    data.getFloat("spread"),
                    data.getInt("fire_time"),
                    data.getBoolean("no_gravity"),
                    data.getBoolean("critical"),
                    data.getBoolean("has_owner"),
                    data.get("projectile_action"),
                    data.getInt("count")
            ),
            (action, serializableData) -> serializableData.instance()
    );

    private final float damage, speed, spread;
    private final int fireTime, count;
    private final boolean noGravity, critical, hasOwner;
    private final Optional<EntityAction> projectileAction;

    public FireArrowAction(float damage, float speed, float spread, int fireTime,
                           boolean noGravity, boolean critical, boolean hasOwner,
                           Optional<EntityAction> projectileAction, int count) {
        this.damage = damage;
        this.speed = speed;
        this.spread = spread;
        this.fireTime = fireTime;
        this.noGravity = noGravity;
        this.critical = critical;
        this.hasOwner = hasOwner;
        this.projectileAction = projectileAction;
        this.count = count;
    }

    @Override
    public void accept(EntityActionContext context) {
        if (!(context.entity() instanceof LivingEntity livingEntity)) return;
        for (int i = 0; i < count; i++) {
            spawnFireArrow(livingEntity);
        }
    }

    private void spawnFireArrow(LivingEntity owner) {
        ArrowItem arrowItem = (ArrowItem) Items.ARROW;
        ItemStack itemStack = new ItemStack(arrowItem);
        PersistentProjectileEntity arrow = arrowItem.createArrow(owner.getWorld(), itemStack, hasOwner ? owner : null);
        if (fireTime > 0) arrow.setOnFireFor(fireTime);
        if (noGravity) arrow.setNoGravity(true);
        arrow.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0.0F, speed, spread);
        arrow.setDamage(damage);
        if (critical) arrow.setCritical(true);
        arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        if (owner.getWorld().spawnEntity(arrow)) {
            projectileAction.ifPresent(a -> a.accept(arrow));
        }
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return AdditionalEntityActions.FIRE_ARROW;
    }

    public static ActionConfiguration<FireArrowAction> createConfig(Identifier id) {
        return ActionConfiguration.of(id, DATA_FACTORY);
    }

    public static void registerAction(Consumer<ActionConfiguration<FireArrowAction>> actionReg,
                                       Consumer<?> biActionReg) {
        actionReg.accept(createConfig(ShapeShifterCurseFabric.identifier("fire_arrow")));
    }
}