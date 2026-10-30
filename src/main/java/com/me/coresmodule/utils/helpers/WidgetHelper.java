package com.me.coresmodule.utils.helpers;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import static com.me.coresmodule.CoresModule.mc;

/**
 * Static drawing/packing helpers for {@link com.me.coresmodule.utils.render.hud.widget.Widget}.
 * Split out from the widget class itself so the box-drawing math doesn't clutter it,
 * same idea as Helper/ItemHelper/TextHelper.
 */
public class WidgetHelper {

    public static final int BORDER_N;
    public static final int BORDER_S = 4;
    public static final int BORDER_W = 4;
    public static final int BORDER_E = 4;

    public static final int DEFAULT_BG = 0xC00C0C0C;
    public static final int MINIMAL_BG = 0x64000000;

    static {
        BORDER_N = mc.font.lineHeight + 2;
    }

    public static void drawBackground(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean minimal) {
        int bg = minimal ? MINIMAL_BG : DEFAULT_BG;
        graphics.fill(x + 1, y, x + w - 1, y + h, bg);
        graphics.fill(x, y + 1, x + 1, y + h - 1, bg);
        graphics.fill(x + w - 1, y + 1, x + w, y + h - 1, bg);
    }

    public static void drawBorder(GuiGraphicsExtractor graphics, int x, int y, int w, int h, Font font, String title, int color) {
        int strHeightHalf = font.lineHeight / 2;
        int strAreaWidth = font.width(title) + 4;

        drawHLine(graphics, x + 2, y + 1 + strHeightHalf, 4, color);
        drawHLine(graphics, x + 2 + strAreaWidth + 4, y + 1 + strHeightHalf, w - 4 - 4 - strAreaWidth, color);
        drawHLine(graphics, x + 2, y + h - 2, w - 4, color);

        drawVLine(graphics, x + 1, y + 2 + strHeightHalf, h - 4 - strHeightHalf, color);
        drawVLine(graphics, x + w - 2, y + 2 + strHeightHalf, h - 4 - strHeightHalf, color);
    }

    public static void drawHLine(GuiGraphicsExtractor graphics, int x, int y, int width, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
    }

    public static void drawVLine(GuiGraphicsExtractor graphics, int x, int y, int height, int color) {
        graphics.fill(x, y, x + 1, y + height, color);
    }

    /**
     * @return the packed [width, height] for a box containing the given lines and title,
     * respecting the border sizes above.
     */
    public static int[] pack(Font font, String title, java.util.List<String> lines) {
        int w = 0;
        int h = 0;

        for (String line : lines) {
            h += font.lineHeight + 1;
            w = Math.max(w, font.width(line));
        }

        h += BORDER_N + BORDER_S - 2;
        w += BORDER_W + BORDER_E;
        w = Math.max(w, BORDER_W + BORDER_E + font.width(title) + 8);

        return new int[]{w, h};
    }
}
