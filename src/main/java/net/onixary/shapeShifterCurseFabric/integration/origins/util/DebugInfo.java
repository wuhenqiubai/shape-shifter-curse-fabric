package net.onixary.shapeShifterCurseFabric.integration.origins.util;

import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayers;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;

public class DebugInfo {

    public static void printRegistrySizes(String at) {
        printInfo(new String[] {
            "Registry Size at " + at,
            "Origins: " + OriginRegistry.size(),
            "Layers:  " + OriginLayers.size(),
            "Powers:  " + PowerTypeRegistry.size()
        });
    }
    private static void printInfo(String[] lines) {
        int longest = 0;
        for(int i = 0; i < lines.length; i++) {
            if(lines[i].length() > longest)
                longest = lines[i].length();
            lines[i] = "| " + lines[i];
        }
	    StringBuilder border = new StringBuilder("+");
	    border.repeat("-", Math.max(0, longest + 2));
	    border.append("+");
	    Origins.LOGGER.info(border.toString());
        for(int i = 0; i < lines.length; i++) {
            while(lines[i].length() < longest + 3)
                lines[i] += " ";
            lines[i] += "|";
            Origins.LOGGER.info(lines[i]);
        }
	    Origins.LOGGER.info(border.toString());
    }
}
