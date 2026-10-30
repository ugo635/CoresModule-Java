package com.me.coresmodule.features.safari;

import com.me.coresmodule.utils.render.hud.widget.Widget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;
import java.util.List;

import static com.me.coresmodule.CoresModule.mc;

/**
 * Specialized Critter Safari widget renderer with scaled text + geometry.
 */
public class CritterSafariWidget extends Widget {

    private static final Color PANEL_BG = new Color(16, 19, 23, 200);
    private static final Color PANEL_BORDER = new Color(116, 0, 255);
    private static final Color SUBTLE_BORDER = new Color(52, 58, 66);
    private static final Color TILE_BG = new Color(31, 36, 42);
    private static final Color TEXT = new Color(216, 221, 228);
    private static final Color MUTED = new Color(148, 157, 167);

    private static final Color AREA_FOREST = new Color(52, 98, 64);
    private static final Color AREA_ICY = new Color(40, 79, 84);
    private static final Color AREA_CAVERN = new Color(90, 66, 40);
    private static final Color AREA_HAUNTED = new Color(94, 64, 114);

    private static final Color ROW_DEFAULT = new Color(38, 42, 48, 192);
    private static final Color ROW_RED = new Color(122, 51, 51, 192);
    private static final Color ROW_GREEN = new Color(54, 104, 66, 192);
    private static final Color ROW_GOLD = ROW_GREEN; //new Color(255, 161, 0, 192);
    // Uses green instead of gold for when it's enough for those with random amounts where gold meant we reached the max

    private static final Color AREA_BLOCK_BG_BASE = new Color(18, 20, 24);
    private static final double AREA_BLOCK_TINT = 0.30;

    // base layout units (unscaled)
    private static final int P = 4;
    private static final int GAP = 3;
    private static final int TILE_HEIGHT = 26;
    private static final int HEADER_HEIGHT = 12;
    private static final int AREA_TILE_HEIGHT = 12;
    private static final int ROW_HEIGHT = 11;

    public CritterSafariWidget(float scale) {
        super("critter_safari_widget", "Critter Safari", PANEL_BORDER, scale);
        this.x = 10;
        this.y = 10;
    }

    @Override
    public void updateContent() {
        addLine(" ");
    }

    @Override
    public void render(GuiGraphicsExtractor graphics) {
        if (!checkCondition()) return;
        if (mc.player == null) return;

        Font font = mc.font;
        CritterSafari.CritterSafariSnapshot snap = CritterSafari.snapshotForWidget();

        int leftColW = 122;
        int rightColW = 122;
        int innerW = leftColW + GAP + rightColW;
        int panelWBase = innerW + (P * 2);

        int tileW = (innerW - (GAP * 2)) / 3;
        int topBlockH = HEADER_HEIGHT + GAP + TILE_HEIGHT + GAP + TILE_HEIGHT;

        int areaBlockW = (innerW - GAP) / 2;
        int topAreasH = AREA_TILE_HEIGHT + (9 * ROW_HEIGHT);
        int secondRowH = Math.max(AREA_TILE_HEIGHT + (9 * ROW_HEIGHT), AREA_TILE_HEIGHT + (10 * ROW_HEIGHT));
        int areasTotalH = topAreasH + GAP + secondRowH;
        int panelHBase = P + topBlockH + GAP + areasTotalH + P;

        // scale pose to scale text + geometry together
        graphics.pose().pushMatrix();
        graphics.pose().scale(getScale(), getScale());

        int bx = Math.round(getX() / getScale());
        int by = Math.round(getY() / getScale());

        graphics.fill(bx, by, bx + panelWBase, by + panelHBase, PANEL_BG.getRGB());
        drawRect(graphics, bx, by, panelWBase, panelHBase, PANEL_BORDER);

        graphics.text(font, snap.runHeaderText(), bx + P, by + P, TEXT.getRGB(), false);

        List<CritterSafari.PlayerView> players = snap.players();
        String[] tileText = new String[6];
        Color[] tileBg = new Color[6];

        for (int i = 0; i < 4; i++) {
            if (i < players.size()) {
                CritterSafari.PlayerView pv = players.get(i);
                tileText[i] = pv.name() + ": " + pv.count() + " | " + pv.biome();
                tileBg[i] = biomeBg(pv.biome());
            } else {
                tileText[i] = "Waiting... | Unknown";
                tileBg[i] = TILE_BG;
            }
        }
        tileText[4] = "Floor drops: " + snap.floorDrops();
        tileBg[4] = TILE_BG;
        tileText[5] = "Runs since shiny: " + snap.runsSinceShiny();
        tileBg[5] = TILE_BG;

        int tilesStartY = by + P + HEADER_HEIGHT + GAP;
        for (int i = 0; i < 6; i++) {
            int row = i / 3;
            int col = i % 3;
            int tx = bx + P + col * (tileW + GAP);
            int ty = tilesStartY + row * (TILE_HEIGHT + GAP);
            drawTile(graphics, font, tx, ty, tileW, TILE_HEIGHT, tileBg[i], tileText[i]);
        }

        List<CritterSafari.AreaView> areas = snap.areas();
        CritterSafari.AreaView forest = findArea(areas, "Forest");
        CritterSafari.AreaView icy = findArea(areas, "Icy");
        CritterSafari.AreaView cavern = findArea(areas, "Cavern");
        CritterSafari.AreaView haunted = findArea(areas, "Haunted");

        int areaTopY = by + P + topBlockH + GAP;
        int areaXLeft = bx + P;
        int areaXRight = areaXLeft + areaBlockW + GAP;

        drawAreaBlock(graphics, font, areaXLeft, areaTopY, areaBlockW, forest, AREA_FOREST);
        drawAreaBlock(graphics, font, areaXRight, areaTopY, areaBlockW, icy, AREA_ICY);

        int secondRowY = areaTopY + topAreasH + GAP;
        drawAreaBlock(graphics, font, areaXLeft, secondRowY, areaBlockW, cavern, AREA_CAVERN);
        drawAreaBlock(graphics, font, areaXRight, secondRowY, areaBlockW, haunted, AREA_HAUNTED);

        graphics.pose().popMatrix();
    }

    private static CritterSafari.AreaView findArea(List<CritterSafari.AreaView> areas, String name) {
        for (CritterSafari.AreaView area : areas) {
            if (area.name().equals(name)) return area;
        }
        return new CritterSafari.AreaView(name, 0, List.of());
    }

    private void drawAreaBlock(
            GuiGraphicsExtractor g,
            Font font,
            int bx,
            int by,
            int bw,
            CritterSafari.AreaView area,
            Color titleBg
    ) {
        int rows = area.shards().size();
        int bh = AREA_TILE_HEIGHT + rows * ROW_HEIGHT;

        g.fill(bx, by, bx + bw, by + bh, blockBgFor(titleBg).getRGB());
        drawRect(g, bx, by, bw, bh, SUBTLE_BORDER);

        String title = area.name() + ": " + area.total();
        g.fill(bx + 1, by + 1, bx + bw - 1, by + AREA_TILE_HEIGHT, titleBg.getRGB());
        g.text(font, title, bx + 4, by + 2, TEXT.getRGB(), false);

        int yRow = by + AREA_TILE_HEIGHT;
        for (CritterSafari.ShardView shard : area.shards()) {
            Color bg = rowBg(shard.colorCode());
            g.fill(bx + 1, yRow, bx + bw - 1, yRow + ROW_HEIGHT, bg.getRGB());

            String name = shard.name();
            String count = String.valueOf(shard.count());

            g.text(font, name, bx + 4, yRow + 2, TEXT.getRGB(), false);
            int countW = font.width(count);
            g.text(font, count, bx + bw - 4 - countW, yRow + 2, TEXT.getRGB(), false);

            yRow += ROW_HEIGHT;
        }
    }

    private void drawTile(
            GuiGraphicsExtractor g,
            Font font,
            int tx,
            int ty,
            int tw,
            int th,
            Color bg,
            String text
    ) {
        g.fill(tx, ty, tx + tw, ty + th, bg.getRGB());
        drawRect(g, tx, ty, tw, th, SUBTLE_BORDER);

        int maxTextW = tw - 6;
        List<String> lines = wrapToTwoLines(font, text, maxTextW);

        if (lines.size() == 1) {
            int textY = ty + (th - font.lineHeight) / 2;
            g.text(font, lines.get(0), tx + 3, textY, MUTED.getRGB(), false);
        } else {
            int lineGap = font.lineHeight + 1;
            int blockH = lineGap * lines.size() - 1;
            int textY = ty + (th - blockH) / 2;
            for (String line : lines) {
                g.text(font, line, tx + 3, textY, MUTED.getRGB(), false);
                textY += lineGap;
            }
        }
    }

    /**
     * Wraps text onto a second line (breaking on the nearest space that still fits) rather
     * than truncating with an ellipsis, so labels like "Runs since shiny: 5" stay fully readable.
     * Falls back to a hard cut + ellipsis on the second line if it's still too wide.
     */
    private static List<String> wrapToTwoLines(Font font, String text, int maxWidth) {
        if (font.width(text) <= maxWidth) return List.of(text);

        int splitAt = -1;
        for (int i = text.length() - 1; i > 0; i--) {
            if (text.charAt(i) == ' ' && font.width(text.substring(0, i)) <= maxWidth) {
                splitAt = i;
                break;
            }
        }

        String line1;
        String line2;
        if (splitAt == -1) {
            line1 = text;
            while (font.width(line1) > maxWidth && line1.length() > 1) {
                line1 = line1.substring(0, line1.length() - 1);
            }
            line2 = text.substring(line1.length());
        } else {
            line1 = text.substring(0, splitAt);
            line2 = text.substring(splitAt + 1);
        }

        while (font.width(line2) > maxWidth && line2.length() > 3) {
            line2 = line2.substring(0, line2.length() - 1);
        }
        if (font.width(line2) > maxWidth) line2 = line2.substring(0, line2.length() - 1) + "…";

        return List.of(line1, line2);
    }

    /**
     * Blends an area's accent color into a near-black base so each area block reads as
     * "tinted dark [area color]" (e.g. dark aqua for Icy, dark purple for Haunted) instead
     * of every area sharing one flat neutral background.
     */
    private static Color blockBgFor(Color accent) {
        int r = (int) (AREA_BLOCK_BG_BASE.getRed() * (1 - AREA_BLOCK_TINT) + accent.getRed() * AREA_BLOCK_TINT);
        int g = (int) (AREA_BLOCK_BG_BASE.getGreen() * (1 - AREA_BLOCK_TINT) + accent.getGreen() * AREA_BLOCK_TINT);
        int b = (int) (AREA_BLOCK_BG_BASE.getBlue() * (1 - AREA_BLOCK_TINT) + accent.getBlue() * AREA_BLOCK_TINT);
        return new Color(r, g, b);
    }

    private static Color biomeBg(String biome) {
        return switch (biome) {
            case "Forest" -> AREA_FOREST;
            case "Icy" -> AREA_ICY;
            case "Cavern" -> AREA_CAVERN;
            case "Haunted" -> AREA_HAUNTED;
            default -> TILE_BG;
        };
    }

    private static Color rowBg(String colorCode) {
        if ("§a".equals(colorCode)) return ROW_GREEN;
        if ("§c".equals(colorCode)) return ROW_RED;
        if ("§6".equals(colorCode)) return ROW_GOLD;
        return ROW_DEFAULT;
    }

    private static void drawRect(GuiGraphicsExtractor g, int x, int y, int w, int h, Color color) {
        int rgb = color.getRGB();
        g.fill(x, y, x + w, y + 1, rgb);
        g.fill(x, y + h - 1, x + w, y + h, rgb);
        g.fill(x, y, x + 1, y + h, rgb);
        g.fill(x + w - 1, y, x + w, y + h, rgb);
    }
}