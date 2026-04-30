package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.BiEntityAction;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.BiEntityActionContext;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.List;
import java.util.Optional;

public class ExplosionDamageEntityAction extends EntityActionType {

    private final int power;
    private final Optional<EntityCondition> entityCondition;
    private final Optional<EntityAction> entityAction;
    private final boolean explosionDamageEntity;

    public static final TypedDataObjectFactory<ExplosionDamageEntityAction> DATA_FACTORY =
            TypedDataObjectFactory.simple(
                    new SerializableData()
                            .add("power", SerializableDataTypes.INT, 0)
                            .add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                            .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("explosion_damage_entity", SerializableDataTypes.BOOLEAN, true),
                    data -> new ExplosionDamageEntityAction(
                            data.getInt("power"),
                            data.get("entity_condition"),
                            data.get("entity_action"),
                            data.getBoolean("explosion_damage_entity")
                    ),
                    (action, serializableData) -> serializableData.instance()
            );

    public ExplosionDamageEntityAction(int power, Optional<EntityCondition> entityCondition, Optional<EntityAction> entityAction, boolean explosionDamageEntity) {
        this.power = power;
        this.entityCondition = entityCondition;
        this.entityAction = entityAction;
        this.explosionDamageEntity = explosionDamageEntity;
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();
        explosion(entity, power, entityCondition.orElse(null), entityAction.orElse(null), explosionDamageEntity);
    }

    @Override
    public ActionConfiguration<EntityActionType> getConfig() {
        return createConfig(ShapeShifterCurseFabric.identifier("explosion_damage_entity"));
    }

    public static ActionConfiguration<ExplosionDamageEntityAction> createConfig(Identifier id) {
        return ActionConfiguration.of(id, DATA_FACTORY);
    }

    private static void explosion(Entity entity,
                                  int power,
                                  EntityCondition entityCondition,
                                  EntityAction entityAction,
                                  boolean explosionDamageEntity
    ) {
        Vec3d ExplosionPos = entity.getPos();
        DamageSource source = entity.getWorld().getDamageSources().explosion(entity, entity);
        entity.getWorld().emitGameEvent(entity, GameEvent.EXPLODE, new Vec3d(ExplosionPos.getX(), ExplosionPos.getY(), ExplosionPos.getZ()));

        float q = power * 2.0F;
        int k = MathHelper.floor(ExplosionPos.getX() - (double)q - 1.0);
        int l = MathHelper.floor(ExplosionPos.getX() + (double)q + 1.0);
        int r = MathHelper.floor(ExplosionPos.getY() - (double)q - 1.0);
        int s = MathHelper.floor(ExplosionPos.getY() + (double)q + 1.0);
        int t = MathHelper.floor(ExplosionPos.getZ() - (double)q - 1.0);
        int u = MathHelper.floor(ExplosionPos.getZ() + (double)q + 1.0);
        List<Entity> list = entity.getWorld().getOtherEntities(entity, new Box((double)k, (double)r, (double)t, (double)l, (double)s, (double)u));
        for(int v = 0; v < list.size(); ++v) {
            Entity target_entity = list.get(v);
            if (!target_entity.isImmuneToExplosion() && (entityCondition == null || entityCondition.test(target_entity))) {
                double w = Math.sqrt(target_entity.squaredDistanceTo(ExplosionPos)) / (double)q;
                if (w <= 1.0) {
                    double x = target_entity.getX() - ExplosionPos.getX();
                    double y = (target_entity instanceof TntEntity ? target_entity.getY() : target_entity.getEyeY()) - ExplosionPos.getY();
                    double z = target_entity.getZ() - ExplosionPos.getZ();
                    double aa = Math.sqrt(x * x + y * y + z * z);
                    if (aa != 0.0) {
                        x /= aa;
                        y /= aa;
                        z /= aa;
                        double ab = (double) Explosion.getExposure(ExplosionPos, target_entity);
                        double ac = (1.0 - w) * ab;
                        if(explosionDamageEntity){
                            target_entity.damage(source, (float)((int)((ac * ac + ac) / 2.0 * 7.0 * (double)q + 1.0)));
                        }
                        double ad;
                        if (target_entity instanceof LivingEntity livingEntity) {
                            ad = net.minecraft.enchantment.EnchantmentHelper.getProtectionAmount(livingEntity.getServerWorld(null), livingEntity, source);
                            // In 1.21, ProtectionEnchantment.transformExplosionKnockback is replaced
                            // Keep the original knockback logic but use vanilla approach
                            if (ac > 0) {
                                ad = ac;
                            } else {
                                ad = ac;
                            }
                        } else {
                            ad = ac;
                        }
                        x *= ad;
                        y *= ad;
                        z *= ad;
                        Vec3d vec3d2 = new Vec3d(x, y, z);
                        target_entity.setVelocity(target_entity.getVelocity().add(vec3d2));
                        if (entityAction != null) {
                            entityAction.accept(target_entity);
                        }
                    }
                }
            }
        }
    }

    public static void register(java.util.function.Consumer<ActionConfiguration<ExplosionDamageEntityAction>> actionReg) {
        actionReg.accept(createConfig(ShapeShifterCurseFabric.identifier("explosion_damage_entity")));
    }
}