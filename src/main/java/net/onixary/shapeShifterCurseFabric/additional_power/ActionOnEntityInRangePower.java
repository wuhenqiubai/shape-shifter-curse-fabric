package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Apoli 2.12 迁移示范:
 * - extends PowerType (不是 Power!)
 * - entity → getHolder()
 * - setTicking(true) → setTicking()
 * - PowerFactory → TypedDataObjectFactory + PowerType.createConditionedDataFactory()
 * - ActionFactory<Entity>.Instance → EntityAction
 * - ApoliDataTypes.ENTITY_ACTION → ApoliDataTypes.action()
 * - ApoliDataTypes.ENTITY_CONDITION → ApoliDataTypes.condition()
 */
public class ActionOnEntityInRangePower extends PowerType {

    private final Predicate<Entity> entityCondition;
    private final EntityAction entityAction;
    private final EntityAction selfAction;
    private final float actionRadius;
    private final int detectionInterval;

    private int tickCounter = 0;

    public ActionOnEntityInRangePower(SerializableData.Instance data) {
        super();

        // 从 SerializableData 读取条件/动作 (新 API)
        this.entityCondition = data.get("entity_condition");
        this.entityAction = data.get("entity_action");
        this.selfAction = data.get("self_action");
        this.actionRadius = data.getFloat("action_radius");
        this.detectionInterval = data.getInt("detection_interval");

        this.setTicking();
    }

    @Override
    public void serverTick() {
        // 旧: this.entity → 新: getHolder()
        LivingEntity entity = getHolder();
        if (!(entity instanceof PlayerEntity player) || player.isSpectator()) {
            return;
        }

        if (tickCounter++ % detectionInterval == 0) {
            Box searchBox = Box.from(player.getPos()).expand(actionRadius);
            List<Entity> entities = player.getWorld().getOtherEntities(
                    player,
                    searchBox,
                    e -> entityCondition == null || entityCondition.test(e)
            );

            for (Entity targetEntity : entities) {
                if (targetEntity.isAlive() && !targetEntity.isSpectator()) {
                    if (entityAction != null) {
                        entityAction.accept(new EntityActionContext(targetEntity));
                    }
                }
            }

            if (selfAction != null) {
                selfAction.accept(new EntityActionContext(player));
            }

            // 旧: PowerHolderComponent.syncPower(entity, this.type)
            // 新: PowerHolderComponent.sync(entity)
            PowerHolderComponent.sync(player);
        }
    }

    // ===== 工厂方法 (旧 PowerFactory → 新 TypedDataObjectFactory) =====

    public static TypedDataObjectFactory<ActionOnEntityInRangePower> createFactory() {
        return TypedDataObjectFactory.simple(
                // 数据定义
                new SerializableData()
                        .add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                        .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                        .add("self_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                        .add("action_radius", SerializableDataTypes.FLOAT, 8.0f)
                        .add("detection_interval", SerializableDataTypes.INT, 10),
                // 构造
                ActionOnEntityInRangePower::new,
                // 反序列化
                (power, data) -> data.instance()
        );
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return PowerConfiguration.dataFactory(
                ShapeShifterCurseFabric.identifier("action_on_entity_in_range"),
                createFactory()
        );
    }
}
