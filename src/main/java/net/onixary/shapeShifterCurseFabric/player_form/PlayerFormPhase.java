package net.onixary.shapeShifterCurseFabric.player_form;

public enum PlayerFormPhase {
    PHASE_CLEAR,
    PHASE_0,
    PHASE_1,
    PHASE_2,
    PHASE_3, // 永久形态阶段
    // PHASE_SP只有一个阶段
    PHASE_SP;

    // 形态索引常量
    public static final int INDEX_PRE_ACTIVATE = -2;  // 模组未激活
    public static final int INDEX_BASE_SHIFTER = -1;   // 原始幻形者
    public static final int INDEX_PHASE_0 = 0;         // 第一阶段
    public static final int INDEX_PHASE_1 = 1;         // 第二阶段
    public static final int INDEX_PHASE_2 = 2;         // 第三阶段
    public static final int INDEX_PHASE_3 = 3;         // 永久形态
    public static final int INDEX_PHASE_SP = 5;        // SP形态
}
