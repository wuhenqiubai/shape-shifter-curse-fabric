package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 形态组，将同一系列的形态按索引组织成进化链。
 * <p>
 * 一个形态组代表一条形态进化路径，如 Axolotl 的 {@code axolotl_0 → axolotl_1 → axolotl_2 → axolotl_3}。
 * 组内的 0、1、2 索引对应 {@link PlayerFormPhase#PHASE_0}、{@link PlayerFormPhase#PHASE_1}、{@link PlayerFormPhase#PHASE_2}，
 * CursedMoon 回退和自动进化均通过组内前后导航实现。
 *
 * @see PlayerFormPhase
 * @see PlayerFormBase#setGroup(PlayerFormGroup, int)
 */
public class PlayerFormGroup {
	/**
	 * 形态组唯一 ID。例如 {@code ssc:axolotl}。
	 */
    public Identifier GroupID;
	/** 索引 → 形态实例的映射。索引对应 {@link PlayerFormPhase} 中的 INDEX_* 常量。 */
    public Map<Integer, PlayerFormBase> Forms = new HashMap<>();

	/**
	 * @param GroupID 组 ID，与 {@link RegPlayerForms#registerDynamicPlayerFormGroup} 中注册的一致
     */
    public PlayerFormGroup(Identifier GroupID) {
        this.GroupID = GroupID;
    }

	/**
	 * 向组内添加一个形态。
	 *
	 * @param form  形态实例，会自动调用 {@link PlayerFormBase#setGroup(PlayerFormGroup, int)} 绑定
	 * @param Index 形态索引，通常使用 {@link PlayerFormPhase#INDEX_PHASE_0}、{@link PlayerFormPhase#INDEX_PHASE_1} 等
	 * @return this（链式调用）
	 * @throws IllegalArgumentException 如果该形态已被其他组绑定
     */
    public PlayerFormGroup addForm(PlayerFormBase form, int Index) {
        Forms.put(Index, form);
        form.setGroup(this, Index);
        return this;
    }

	/**
	 * 获取指定索引的形态。
	 *
	 * @param Index 形态索引
	 * @return 形态实例，如果该索引注册的形态已从注册表中移除，则自动清理并返回 null
     */
    public PlayerFormBase getForm(int Index) {
        PlayerFormBase form = Forms.get(Index);
        if (!RegPlayerForms.playerForms.containsKey(form.FormID) && !(form.getGroup() == this) && !(form.getIndex() == Index)) {
            Forms.remove(Index);
            ShapeShifterCurseFabric.LOGGER.warn("Form {} is not registered in the registry, removing it from group {}", form.FormID, GroupID);
            return null;
        }
        return Forms.get(Index);
    }

	/**
	 * 获取指定形态在组内的索引。
	 *
	 * @param form 形态实例
	 * @return 索引的 Optional，不在组内则返回 empty
     */
    public Optional<Integer> getFormIndex(PlayerFormBase form) {
        return getFormIndex(form.FormID);
    }

	/**
	 * 获取指定形态 ID 在组内的索引。
	 *
	 * @param formID 形态 ID
	 * @return 索引的 Optional，不在组内则返回 empty
     */
    public Optional<Integer> getFormIndex(Identifier formID) {
        for (Map.Entry<Integer, PlayerFormBase> entry : Forms.entrySet()) {
            if (entry.getValue().FormID.equals(formID) && getForm(entry.getKey()) != null) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

	/** @return 组内是否存在指定 ID 的形态 */
    public boolean hasForm(Identifier formID) {
        return getFormIndex(formID).isPresent();
    }

	/** @return 组内是否存在指定形态 */
    public boolean hasForm(PlayerFormBase form) {
        return getFormIndex(form).isPresent();
    }

	/** @return 组内是否存在指定索引 */
    public boolean hasForm(int Index) {
        return Forms.containsKey(Index);
    }

	/**
	 * 获取进化链中的下一个形态（索引 +1）。
	 * 用于 CursedMoon 触发回退时确定目标。
	 *
	 * @param form 当前形态
	 * @return 下一个形态，若已是链尾则返回 null
     */
    public PlayerFormBase getNextForm(PlayerFormBase form) {
        return getFormIndex(form).map(index -> getForm(index + 1)).orElse(null);
    }

	/**
	 * 获取进化链中的上一个形态（索引 -1）。
	 * 用于自动进化或手动升级时确定目标。
	 *
	 * @param form 当前形态
	 * @return 上一个形态，若已是链首则返回 null
     */
    public PlayerFormBase getPrevForm(PlayerFormBase form) {
        return getFormIndex(form).map(index -> getForm(index - 1)).orElse(null);
    }
}
