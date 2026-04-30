package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.BiEntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpawnParticlesInCircleAction extends EntityActionType {

    public static final TypedDataObjectFactory<SpawnParticlesInCircleAction> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
                    .add("bientity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
                    .add("count", SerializableDataTypes.INT, 1)
                    .add("speed", SerializableDataTypes.FLOAT, 0.0F)
                    .add("force", SerializableDataTypes.BOOLEAN, false)
                    .add("spread", SerializableDataTypes.VECTOR, new Vec3d(0.5, 0.5, 0.5))
                    .add("offset_x", SerializableDataTypes.DOUBLE, 0.0D)
                    .add("offset_y", SerializableDataTypes.DOUBLE, 0.5D)
                    .add("offset_z", SerializableDataTypes.DOUBLE, 0.0D)
                    .add("radius", SerializableDataTypes.DOUBLE, 1.0D)
                    .add("sample_count", SerializableDataTypes.INT, 8),
            data -> new SpawnParticlesInCircleAction(
                    data.get("particle"),
                    data.get("bientity_condition"),
                    data.getInt("count"),
                    data.getFloat("speed"),
                    data.getBoolean("force"),
                    data.get("spread"),
                    data.getDouble("offset_x"),
                    data.getDouble("offset_y"),
                    data.getDouble("offset_z"),
                    data.getDouble("radius"),
                    data.getInt("sample_count")
            ),
            (actionType, serializableData) -> serializableData.instance()
                    .set("particle", actionType.particleEffect)
                    .set("bientity_condition", actionType.biEntityCondition)
                    .set("count", actionType.count)
                    .set("speed", actionType.speed)
                    .set("force", actionType.force)
                    .set("spread", actionType.spread)
                    .set("offset_x", actionType.offsetX)
                    .set("offset_y", actionType.offsetY)
                    .set("offset_z", actionType.offsetZ)
                    .set("radius", actionType.radius)
                    .set("sample_count", actionType.sampleCount)
    );

    private final ParticleEffect particleEffect;
    private final Optional<EntityCondition> biEntityCondition;
    private final int count;
    private final float speed;
    private final boolean force;
    private final Vec3d spread;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double radius;
    private final int sampleCount;

    public SpawnParticlesInCircleAction(ParticleEffect particleEffect, Optional<EntityCondition> biEntityCondition,
                                        int count, float speed, boolean force, Vec3d spread,
                                        double offsetX, double offsetY, double offsetZ,
                                        double radius, int sampleCount) {
        this.particleEffect = particleEffect;
        this.biEntityCondition = biEntityCondition;
        this.count = count;
        this.speed = speed;
        this.force = force;
        this.spread = spread;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.radius = radius;
        this.sampleCount = sampleCount;
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();

        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        int actualCount = Math.max(0, this.count);
        int actualSampleCount = Math.max(1, this.sampleCount);

        Vec3d entityPos = entity.getPos();
        Vec3d basePos = entityPos.add(this.offsetX, this.offsetY, this.offsetZ);
        Vec3d delta = this.spread.multiply(entity.getWidth(), entity.getEyeHeight(entity.getPose()), entity.getWidth());

        for (int i = 0; i < actualSampleCount; i++) {
            double angle = 2 * Math.PI * i / actualSampleCount;
            double xOffset = this.radius * Math.cos(angle);
            double zOffset = this.radius * Math.sin(angle);
            Vec3d particlePos = basePos.add(xOffset, 0, zOffset);

            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                if (this.biEntityCondition.isEmpty() || this.biEntityCondition.get().test(player)) {
                    serverWorld.spawnParticles(
                            player,
                            this.particleEffect,
                            this.force,
                            particlePos.getX(),
                            particlePos.getY(),
                            particlePos.getZ(),
                            actualCount,
                            delta.getX(),
                            delta.getY(),
                            delta.getZ(),
                            this.speed
                    );
                }
            }
        }
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return AdditionalEntityActions.SPAWN_PARTICLES_IN_CIRCLE;
    }

    public static ActionConfiguration<SpawnParticlesInCircleAction> createConfig(Identifier id) {
        return ActionConfiguration.of(id, DATA_FACTORY);
    }
}
