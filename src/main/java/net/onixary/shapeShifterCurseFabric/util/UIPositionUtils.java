package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;

@Environment(EnvType.CLIENT)
public class UIPositionUtils {

    // 矫正点
    // 1 2 3
    // 4 5 6
    // 7 8 9
    // 额外XY偏移量

    public static Pair<Integer, Integer> getCorrectPosition(int positionType, int extraX, int extraY) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return new Pair<>(0, 0);
        }

        int windowWidth = client.getWindow().getScaledWidth();
        int windowHeight = client.getWindow().getScaledHeight();

        int centerX = windowWidth / 2;
        int centerY = windowHeight / 2;

        int posX = 0;
        int posY = 0;
        
        switch (positionType) {
            case 1 -> { // 左上角
                posX = 0;
                posY = 0;
            }
            case 2 -> { // 上中
                posX = centerX;
                posY = 0;
            }
            case 3 -> { // 右上角
                posX = windowWidth;
                posY = 0;
            }
            case 4 -> { // 左中
                posX = 0;
                posY = centerY;
            }
            case 5 -> { // 中心
                posX = centerX;
                posY = centerY;
            }
            case 6 -> { // 右中
                posX = windowWidth;
                posY = centerY;
            }
            case 7 -> { // 左下角
                posX = 0;
                posY = windowHeight;
            }
            case 8 -> { // 下中
                posX = centerX;
                posY = windowHeight;
            }
            case 9 -> { // 右下角
                posX = windowWidth;
                posY = windowHeight;
            }
            default -> { // 默认中心
                posX = centerX;
                posY = centerY;
            }
        }

        return new Pair<>(posX + extraX, posY + extraY);
    }
}
