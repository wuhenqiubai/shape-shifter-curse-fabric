package net.onixary.shapeShifterCurseFabric.minion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface IPlayerEntityMinion {
	ConcurrentHashMap<Identifier, ArrayList<UUID>> shape_shifter_curse$getAllMinions();

	ArrayList<UUID> shape_shifter_curse$getMinionsByMinionID(Identifier MinionID);

	int shape_shifter_curse$getMinionsCount();

	int shape_shifter_curse$getMinionsCount(Identifier MinionID);

	boolean shape_shifter_curse$minionExist(Identifier MinionID, UUID minionUUID);

	void shape_shifter_curse$removeMinion(Identifier MinionID, UUID minionUUID);

	<T extends IMinion<? extends LivingEntity>> void shape_shifter_curse$addMinion(T minion);

	void shape_shifter_curse$applyCooldown(Identifier MinionID, long time);

	long shape_shifter_curse$getCooldownTime(Identifier MinionID);

	void shape_shifter_curse$resetAllCooldown();

	void shape_shifter_curse$clearAllMinions();

	void shape_shifter_curse$clearMinions(Identifier MinionID);
}
