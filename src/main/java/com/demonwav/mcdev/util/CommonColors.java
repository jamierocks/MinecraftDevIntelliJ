package com.demonwav.mcdev.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

public final class CommonColors {

    @NotNull private static final Color DARK_RED = new Color(0xAA0000);
    @NotNull private static final Color RED = new Color(0xFF5555);
    @NotNull private static final Color GOLD = new Color(0xFFAA00);
    @NotNull private static final Color YELLOW = new Color(0xFFFF55);
    @NotNull private static final Color DARK_GREEN = new Color(0x00AA00);
    @NotNull private static final Color GREEN = new Color(0x55FF55);
    @NotNull private static final Color AQUA = new Color(0x55FFFF);
    @NotNull private static final Color DARK_AQUA = new Color(0x00AAAA);
    @NotNull private static final Color DARK_BLUE = new Color(0x0000AA);
    @NotNull private static final Color BLUE = new Color(0x5555FF);
    @NotNull private static final Color LIGHT_PURPLE = new Color(0xFF55FF);
    @NotNull private static final Color DARK_PURPLE = new Color(0xAA00AA);
    @NotNull private static final Color WHITE = new Color(0xFFFFFF);
    @NotNull private static final Color GRAY = new Color(0xAAAAAA);
    @NotNull private static final Color DARK_GRAY = new Color(0x555555);
    @NotNull private static final Color BLACK = new Color(0x000000);

    @NotNull private static final ImmutableMap<String, Color> colorMap = ImmutableMap.<String, Color>builder()
            .put("DARK_RED", DARK_RED)
            .put("RED", RED)
            .put("GOLD", GOLD)
            .put("YELLOW", YELLOW)
            .put("DARK_GREEN", DARK_GREEN)
            .put("GREEN", GREEN)
            .put("AQUA", AQUA)
            .put("DARK_AQUA", DARK_AQUA)
            .put("DARK_BLUE", DARK_BLUE)
            .put("BLUE", BLUE)
            .put("LIGHT_PURPLE", LIGHT_PURPLE)
            .put("DARK_PURPLE", DARK_PURPLE)
            .put("WHITE", WHITE)
            .put("GRAY", GRAY)
            .put("DARK_GRAY", DARK_GRAY)
            .put("BLACK", BLACK).build();

    private CommonColors() {
    }

    public static void applyStandardColors(@NotNull Map<String, Color> map, @NotNull String prefix) {
        colorMap.forEach((key, value) -> map.put(prefix + "." + key, value));
    }

    public static ImmutableMap<String, Color> getColorMap() {
        return colorMap;
    }
}
