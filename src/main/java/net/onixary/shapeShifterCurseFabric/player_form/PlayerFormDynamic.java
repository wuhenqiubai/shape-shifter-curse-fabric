package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.util.IdentifierAlias;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mixin.accessor.PowerTypeRegistryAccessor;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.render.form_render.FormModelResourceReloadListener;
import net.onixary.shapeShifterCurseFabric.render.form_render.FormRenderUtils;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 从 JSON 数据包动态加载的形态，继承 {@link PlayerFormBase}。
 * <p>
 * 数据包形态完全通过 JSON 定义，支持动画控制器、Power 动画、额外 Origin Power、赞助者限定等功能。
 * 服务端数据包重载后通过 {@link RegPlayerForms} 注册，客户端通过 {@link FormModelResourceReloadListener} 同步模型。
 * <p>
 * JSON 数据包格式示例：
 * <pre>
 * {
 *   "FormID": "ssc:custom_form",
 *   "phase": "PHASE_0",
 *   "bodyType": "FERAL",
 *   "FurModelID": "ssc:models/custom_form.geo.json",
 *   "groupID": "ssc:custom_group",
 *   "groupIndex": 0,
 *   "anim": { ... },
 *   "powerAnim": { ... },
 *   "ExtraPower": [ ... ]
 * }
 * </pre>
 *
 * @see PlayerFormBase
 * @see RegPlayerForms
 */
public class PlayerFormDynamic extends PlayerFormBase{

	/**
	 * 通配 UUID，表示对所有玩家公开。用于 {@link #PlayerUUIDs} 白名单。
	 */
    public static final UUID PublicUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	/** 形态模型的 GeoModel ID，对应 {@link FormModelResourceReloadListener} 中注册的模型 ID。 */
    public Identifier FurModelID = null;
	/** 额外 Origin Power ID 列表。这些 power 会在形态激活时一并赋予玩家。 */
	public List<Identifier> ExtraPower = new LinkedList<>();
	/** 额外 Origin Power 的原始 JSON 数据，键为注册后的 power ID。 */
    public HashMap<Identifier, JsonObject> ExtraPowerData = new LinkedHashMap<>();
    private int TempPowerIndex = 0;
	/**
	 * 是否是赞助者限定形态（可以使用特殊物品直接变形）。
	 */
	public boolean IsPatronForm = false;
	/**
	 * 所需的最低赞助等级。赞助等级低于此值的玩家无法使用。
	 */
	public int RequirePatronLevel = 0;
	/** 可使用的玩家 UUID 白名单。为空则不限制；包含 {@link #PublicUUID} 则对所有玩家公开。 */
	public List<UUID> PlayerUUIDs = new ArrayList<>();

    // 覆写数据
    private Identifier originID = null;
    private Identifier originLayerID = null;

    private JsonObject formData = null;  // 我觉得可以不用save出JsonObject 在load的时候直接保存原始JsonObject 省的给一堆的Field写序列化

    private PlayerFormDynamic(Identifier id) {
	    super(id);
    }

    private static boolean _Gson_GetBoolean(JsonObject data, String key, boolean defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsBoolean();
        }
	    return defaultValue;
    }

    private Map<Identifier, AbstractAnimStateController> animStateControllerMap = new HashMap<>();
    private AbstractAnimStateController defaultAnimStateController = AnimUtils.EMPTY_CONTROLLER;
    private Map<Identifier, AnimUtils.AnimationHolderData> powerAnimBuilderMap = new HashMap<>();
    private Map<Identifier, AnimationHolder> powerAnimMap = new HashMap<>();

    private void RegisterAnim(@NotNull Identifier animStateID, @NotNull JsonObject controllerJsonData) {
        AbstractAnimStateController controller = AnimUtils.readController(controllerJsonData);
        animStateControllerMap.put(animStateID, controller);
    }

    private void RegisterPowerAnim(@NotNull Identifier powerAnimID, @NotNull JsonObject powerAnimJsonData) {
        AnimUtils.AnimationHolderData powerAnimData = AnimUtils.readAnim(powerAnimJsonData);
        powerAnimBuilderMap.put(powerAnimID, powerAnimData);
    }

	/**
	 * 创建并加载一个动态形态。
	 *
	 * @param id       形态 ID
	 * @param formData 形态 JSON 数据
	 * @return 已加载的动态形态实例
	 */
    public static PlayerFormDynamic of(Identifier id, JsonObject formData) {
        PlayerFormDynamic form = new PlayerFormDynamic(id);
        form.load(formData);
	    return form;
    }

    @Override
    public void registerPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        for (Identifier powerAnimID : powerAnimBuilderMap.keySet()) {
            AnimUtils.AnimationHolderData powerAnimData = powerAnimBuilderMap.get(powerAnimID);
            powerAnimMap.put(powerAnimID, powerAnimData.build());
        }
        super.registerPowerAnim(player, animSystemData);
    }

    @Override
    public @NotNull Pair<Boolean, @Nullable AnimationHolder> getPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier powerAnimID) {
        if (!this.isModelExist()) {
            return new Pair<>(false, null); // 如果未加载模型则不修改动画
        }
	    boolean isAnimRegistered = powerAnimMap.containsKey(powerAnimID);
        AnimationHolder powerAnimData = powerAnimMap.get(powerAnimID);
        if (isAnimRegistered) {
            return new Pair<>(true, powerAnimData);
        }
        return super.getPowerAnim(player, animSystemData, powerAnimID);
    }

    private static String _Gson_GetString(JsonObject data, String key, String defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsString();
        }
        return defaultValue;
    }

    private static int _Gson_GetInt(JsonObject data, String key, int defaultValue) {
        if (data.has(key)) {
            return data.get(key).getAsInt();
        }
        return defaultValue;
    }

	/**
	 * 从 JSON 数据创建动态形态，FormID 从 JSON 中读取。
	 *
	 * @param formData 包含 {@code FormID} 字段的形态 JSON 数据
	 * @return 已加载的动态形态实例
	 * @throws IllegalArgumentException 如果 JSON 中未包含 FormID
	 */
    public static PlayerFormDynamic of(JsonObject formData) throws IllegalArgumentException {
        PlayerFormDynamic form = new PlayerFormDynamic(null);
        form.load(formData);
        if (form.FormID == null) {
            throw new IllegalArgumentException("FormID is required");
        }
        return form;
    }

	/** @return 此形态的 GeoModel 模型文件是否已加载 */
	public boolean isModelExist() {
		return FormRenderUtils.formRendererRegistry.getOrDefault(FormModelResourceReloadListener.defaultLayer, new HashMap<>()).containsKey(this.getFormOriginID());
    }

	/**
	 * 获取指定动画状态对应的控制器。
	 * <p>
	 * 如果模型未加载则返回空控制器，跳过此状态的动画覆盖。
	 *
	 * @param player         目标玩家
	 * @param animSystemData 动画系统数据
	 * @param animStateID    动画状态 ID
	 * @return 状态控制器，模型未加载或未配置时返回空控制器
	 */
    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        if (!this.isModelExist()) {
            return AnimUtils.EMPTY_CONTROLLER; // 如果未加载模型则不修改动画
        }
        return animStateControllerMap.getOrDefault(animStateID, defaultAnimStateController);
    }

	/**
	 * 从 JSON 对象加载此形态的所有配置数据。
	 * <p>
	 * 包括阶段、体型、动画控制器、Power 动画、Origin 覆盖、形态组、模型 ID、额外 Power、赞助者信息等。
	 *
	 * @param formData 形态 JSON 数据
	 */
    public void load(JsonObject formData) {
        try {
            if (formData.has("FormID")) {
                this.FormID = Identifier.tryParse(formData.get("FormID").getAsString());
            }
            this.setPhase(PlayerFormPhase.valueOf(_Gson_GetString(formData, "phase", "PHASE_CLEAR")));
            this.setBodyType(PlayerFormBodyType.valueOf(_Gson_GetString(formData, "bodyType", "NORMAL")));
            this.setHasSlowFall(_Gson_GetBoolean(formData, "hasSlowFall", false));
            this.setOverrideHandAnim(_Gson_GetBoolean(formData, "overrideHandAnim", false));
            this.setCanSneakRush(_Gson_GetBoolean(formData, "canSneakRush", false));
            this.setCanRushJump(_Gson_GetBoolean(formData, "canRushJump", false));
            this.setIsCustomForm(_Gson_GetBoolean(formData, "isCustomForm", false));
            String originIDStr = _Gson_GetString(formData, "originID", null);
            if (originIDStr != null) {
                this.originID = Identifier.tryParse(originIDStr);
            }
            String originLayerIDStr = _Gson_GetString(formData, "originLayerID", null);
            if (originLayerIDStr != null) {
                this.originLayerID = Identifier.tryParse(originLayerIDStr);
            }
            if (formData.has("anim")) {
                if (formData.get("anim").isJsonObject()) { // 给老版数据包提出需要更新的Log
                    for (Map.Entry<String, JsonElement> entry : formData.get("anim").getAsJsonObject().entrySet()) {
                        if (entry.getValue().isJsonObject()) {
                            Identifier animStateID = Identifier.tryParse(entry.getKey());
                            if (animStateID != null) {
                                this.RegisterAnim(animStateID, entry.getValue().getAsJsonObject());
                            } else {
                                ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid animStateID: {}", this.FormID.toString(), entry.getKey());
                            }
                        } else {
                            ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid animState data: {}", this.FormID.toString(), entry.getValue().toString());
                        }
                    }
                } else {
                    ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Need Update DataPack", this.FormID.toString());
                }
            }
            if (formData.has("powerAnim") && formData.get("powerAnim").isJsonObject())  {
                for (Map.Entry<String, JsonElement> entry : formData.get("powerAnim").getAsJsonObject().entrySet()) {
                    if (entry.getValue().isJsonObject()) {
                        Identifier powerAnimID = Identifier.tryParse(entry.getKey());
                        if (powerAnimID != null) {
                            this.RegisterPowerAnim(powerAnimID, entry.getValue().getAsJsonObject());
                        } else {
                            ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid powerAnimID: {}", this.FormID.toString(), entry.getKey());
                        }
                    } else {
                        ShapeShifterCurseFabric.LOGGER.error("Error while loading player form {}: Invalid powerAnim data: {}", this.FormID.toString(), entry.getValue().toString());
                    }
                }
            }
            if (formData.has("animDefault") && formData.get("animDefault").isJsonObject()) {
                this.defaultAnimStateController = AnimUtils.readController(formData.get("animDefault").getAsJsonObject());
            }
            Identifier GroupID = Identifier.tryParse(_Gson_GetString(formData, "groupID", this.FormID.toString()));
            int GroupIndex = _Gson_GetInt(formData, "groupIndex", 0);
            PlayerFormGroup group = RegPlayerForms.getPlayerFormGroup(GroupID);
            if (group == null) {
                group = RegPlayerForms.registerDynamicPlayerFormGroup(new PlayerFormGroup(GroupID));
            }
            this.setGroup(group, GroupIndex);
            String IDStr = _Gson_GetString(formData, "FurModelID", null);
            this.FurModelID = IDStr == null ? null : Identifier.tryParse(IDStr);
            this.loadExtraPower(formData);
            this.IsPatronForm = _Gson_GetBoolean(formData, "IsPatronForm", false);
            this.PlayerUUIDs.clear();
            if (formData.has("PlayerUUID")) {
                for (JsonElement uuidJson : formData.get("PlayerUUID").getAsJsonArray()) {
                    UUID uuid = UUID.fromString(uuidJson.getAsString());
                    if (uuid != null) {
                        this.PlayerUUIDs.add(uuid);
                    }
                }
            }
            this.RequirePatronLevel = _Gson_GetInt(formData, "RequirePatronLevel", 0);
        }
        catch(Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Error while loading player form: {}", e.getMessage());
        }
	    this.formData = formData;
    }

	/**
	 * 保存此形态的 JSON 配置数据。
	 * <p>
	 * 实际上返回的是 {@link #load} 时保存的原始 JSON 副本，而非重新序列化各字段。
	 * 这样可以保留原始数据格式，避免序列化丢失信息。
	 *
	 * @return 形态 JSON 数据
	 * @throws RuntimeException 如果在 {@link #load} 之前调用
	 */
    public JsonObject save() {
        /*
        JsonObject data = new JsonObject();
        data.addProperty("phase", this.getPhase().toString());
        data.addProperty("bodyType", this.getBodyType().toString());
        data.addProperty("hasSlowFall", this.getHasSlowFall());
        data.addProperty("overrideHandAnim", this.getOverrideHandAnim());
        data.addProperty("canSneakRush", this.getCanSneakRush());
        data.addProperty("canRushJump", this.getCanRushJump());
        data.addProperty("isCustomForm", this.getIsCustomForm());
        if (this.originID != null) {
            data.addProperty("originID", this.originID.toString());
        }
        if (this.originLayerID != null) {
            data.addProperty("originLayerID", this.originLayerID.toString());
        }
        JsonArray anims = new JsonArray();
        for (Map.Entry<PlayerAnimState, AnimationHolderData> entry : animMap_Builder.entrySet()) {
            anims.add(saveAnim(entry.getKey(), entry.getValue()));
        }
        data.add("anim", anims);
        if (this.defaultAnim_Builder != null) {
            data.add("animDefault", saveAnim(null, this.defaultAnim_Builder));
        }
        if (this.getGroup() != null) {
            data.addProperty("groupID", this.getGroup().GroupID.toString());
            data.addProperty("groupIndex", this.FormIndex);
        }
        if (this.FurModelID != null) {
            data.addProperty("FurModelID", this.FurModelID.toString());
        }
        this.saveExtraPower(data);
        data.addProperty("IsPatronForm", this.IsPatronForm);
        if (!PlayerUUIDs.isEmpty()) {
            JsonArray uuids = new JsonArray();
            for (UUID uuid : PlayerUUIDs) {
                uuids.add(uuid.toString());
            }
            data.add("PlayerUUID", uuids);
        }
        data.addProperty("RequirePatronLevel", this.RequirePatronLevel);
        return data;
         */
        if (this.formData == null) {
            throw new RuntimeException("PlayerFormDynamic.save() called before load()");
        }
	    return this.formData;
    }

    @Override
    public Identifier getFormOriginID() {
        return this.originID != null ? this.originID : super.getFormOriginID();
    }

    @Override
    public Identifier getFormOriginLayerID() {
        return this.originLayerID != null ? this.originLayerID : super.getFormOriginLayerID();
    }

	/**
	 * 判断指定玩家是否有权限使用此形态。
	 * <p>
	 * 判断逻辑：如果 {@link #PlayerUUIDs} 白名单中包含该玩家或 {@link #PublicUUID}，
	 * 则该玩家可用。如果白名单为空则默认允许。
	 * 此外还需满足 {@link #RequirePatronLevel} 的赞助等级要求。
	 *
	 * @param player 要判断的玩家
     * @return 该玩家是否可以使用此形态
     */
    public boolean IsPlayerCanUse(PlayerEntity player) {
        // PlayerUUIDs 为白名单 为空则无限制
        if (this.PlayerUUIDs.contains(player.getUuid())) {
            return true;
        }
        return (this.PlayerUUIDs.isEmpty() || this.PlayerUUIDs.contains(PublicUUID)) && (PatronUtils.PatronLevels.getOrDefault(player.getUuid(), 0) >= this.RequirePatronLevel);
    }

	/**
	 * 获取此形态附加的所有额外 Origin Power ID。
	 * <p>
	 * 包含 {@link #ExtraPower} 列表中的预注册 ID，以及 {@link #ExtraPowerData} 中动态注册的 power ID。
	 *
     * @return Power ID 列表
     */
    public List<Identifier> getExtraPower() {
        List<Identifier> powerList = new LinkedList<>(this.ExtraPower);
        // this.ExtraPowerData
        for (Map.Entry<Identifier, JsonObject> powerData : this.ExtraPowerData.entrySet()) {
            powerList.add(powerData.getKey());
        }
        return powerList;
    }

    private Identifier registerPower(JsonObject powerData) {
        Identifier powerID = Identifier.of(this.FormID.getNamespace(), this.FormID.getPath() + "_tpower_" + this.TempPowerIndex);
        if (powerData == null) {
            return null;
        }
        try {
            Identifier PowerID = Identifier.tryParse(powerData.get("type").getAsString());
            PowerFactory pf = null;
            if (IdentifierAlias.GLOBAL.hasAlias(PowerID)) {
                pf = ApoliRegistries.POWER_FACTORY.get(IdentifierAlias.GLOBAL.resolveAlias(PowerID, ApoliRegistries.POWER_FACTORY::containsId));
            } else
            {
                pf = ApoliRegistries.POWER_FACTORY.get(PowerID);
            }
            if (pf == null) {
                ShapeShifterCurseFabric.LOGGER.warn("Power Factory is null! From {}", this.FormID.toString());
                return null;
            }
            PowerFactory.Instance pi = pf.read(powerData);
            PowerType<?> powerType = new PowerType<>(powerID, pi);
            PowerTypeRegistryAccessor.Invoke_Update(powerID, powerType);
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.warn("Failed to register power: {}", powerData.toString());
            return null;
        }
        this.TempPowerIndex++;
        return powerID;
    }

    private void loadExtraPower(JsonObject formData) {
        /* "ExtraPower" : [
         *      "power:power_id",
         *      {
         *          some power data
         *      }
         * ]
         */
        this.ExtraPower.clear();
        this.ExtraPowerData.clear();
        if (!formData.has("ExtraPower")) {
            return;
        }
        JsonArray powerArray = formData.getAsJsonArray("ExtraPower");
        for (JsonElement powerElement : powerArray) {
            if (powerElement.isJsonPrimitive()) {
                this.ExtraPower.add(Identifier.tryParse(powerElement.getAsString()));
            }
            else if (powerElement.isJsonObject()) {
                this.ExtraPowerData.put(registerPower(powerElement.getAsJsonObject()), powerElement.getAsJsonObject());
            }
            else {
                ShapeShifterCurseFabric.LOGGER.warn("Invalid ExtraPower data: {}", powerElement.toString());
            }
        }
    }

    /*
    private void saveExtraPower(JsonObject data) {
        JsonArray powerArray = new JsonArray();
        if (!this.ExtraPower.isEmpty()) {
            for (Identifier powerID : this.ExtraPower) {
                powerArray.add(powerID.toString());
            }
        }
        if (!this.ExtraPowerData.isEmpty()) {
            for (Map.Entry<Identifier, JsonObject> powerData : this.ExtraPowerData.entrySet()) {
                powerArray.add(powerData.getValue());
            }
        }
        data.add("ExtraPower", powerArray);
    }
     */
}
