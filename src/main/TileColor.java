package main;

import java.awt.Color;

public enum TileColor {
    TILE_2(2, new Color(0, 255, 255)), // cyan
    TILE_4(4, new Color(0, 0, 255)), // blue
    TILE_8(8, new Color(0, 255, 0)), // green
    TILE_16(16, new Color(128, 0, 128)), // purple
    TILE_32(32, new Color(255, 165, 0)), // orange
    TILE_64(64, new Color(255, 0, 0)), // red
    TILE_128(128, new Color(255, 255, 0)), // yellow
    TILE_256(256, new Color(255, 50, 50)), // neon red
    TILE_512(512, new Color(200, 0, 255)), // neon purple
    TILE_1024(1024, new Color(50, 150, 255)), // neon blue
    TILE_2048(2048, new Color(255, 255, 80)); // neon yellow

    private final int value;
    private final Color color;

    private static final Color DEFAULT_COLOR = Color.WHITE;

    TileColor(int value, Color color) {
        this.value = value;
        this.color = color;
    }

    int getValue() { return value; }
    Color getColor() { return color; }

    public static Color forValue(int value) {
        for (TileColor tileColor: values()) if (tileColor.getValue() == value) return tileColor.getColor();
        return DEFAULT_COLOR; // default to white
    }
}
