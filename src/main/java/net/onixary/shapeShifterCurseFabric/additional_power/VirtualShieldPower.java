package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VirtualShieldPower extends PowerType {
    private final EntityCondition activeShieldCondition;
    private final EntityAction takenDamageAction;
    private final EntityAction normalDamageAction;
    private final EntityAction shieldBreakAction;

    public static final TypedDataObjectFactory<VirtualShieldPower> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                            .add("active_shield_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                            .add("taken_damage_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("normal_damage_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                            .add("shield_break_action", EntityAction.DATA_TYPE.optional(), Optional.empty()),
                    (data, condition) -> new VirtualShieldPower(
                            data.get("active_shield_condition"),
                            data.get("taken_damage_action"),
                            data.get("normal_damage_action"),
                            data.get("shield_break_action"),
                            condition),
                    (power, sd) -> sd.instance()
                            .set("active_shield_condition", power.activeShieldCondition)
                            .set("taken_damage_action", power.takenDamageAction)
                            .set("normal_damage_action", power.normalDamageAction)
                            .set("shield_break_action", power.shieldBreakAction)
            );

    public VirtualShieldPower(EntityCondition activeShieldCondition, EntityAction takenDamageAction,
                              EntityAction normalDamageAction, EntityAction shieldBreakAction,
                              Optional<EntityCondition> condition) {
        super(condition);
        this.activeShieldCondition = activeShieldCondition;
        this.takenDamageAction = takenDamageAction;
        this.normalDamageAction = normalDamageAction;
        this.shieldBreakAction = shieldBreakAction;
    }

    public boolean blockDamage(DamageSource source) {
        LivingEntity entity = getHolder();
        Vec3d vec3d;
        PersistentProjectileEntity persistentProjectileEntity;
        Entity attacker = source.getSource();
        boolean bl = false;
        if (attacker instanceof PersistentProjectileEntity && (persistentProjectileEntity = (PersistentProjectileEntity)attacker).getPierceLevel() > 0) {
            bl = true;
        }
        if (!source.isIn(DamageTypeTags.BYPASSES_SHIELD) && (this.activeShieldCondition == null || this.activeShieldCondition.test(entity)) && !bl && (vec3d = source.getPosition()) != null) {
            Vec3d vec3d2 = entity.getRotationVec(1.0f);
            Vec3d vec3d3 = vec3d.relativize(entity.getPos()).normalize();
            vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
            if (vec3d3.dotProduct(vec3d2) < 0.0) {
                if (this.takenDamageAction != null) {
                    this.takenDamageAction.accept(entity);
                }
                if (attacker instanceof LivingEntity livingEntity && livingEntity.disablesShield()) {
                    if (this.shieldBreakAction != null) {
                        this.shieldBreakAction.accept(entity);
                    }
                } else {
                    if (this.normalDamageAction != null) {
                        this.normalDamageAction.accept(entity);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override public @NotNull PowerConfiguration<?> getConfig() {
        return createFactory(ShapeShifterCurseFabric.identifier("virtual_shield"));
    }

    public static PowerConfiguration<VirtualShieldPower> createFactory(net.minecraft.util.Identifier id) {
        return PowerConfiguration.of(id, DATA_FACTORY);
    }
}