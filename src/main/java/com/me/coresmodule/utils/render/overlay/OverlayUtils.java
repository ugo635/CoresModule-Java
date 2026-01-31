package com.me.coresmodule.utils.render.overlay;

import net.minecraft.util.Formatting;

public class OverlayUtils {
    public static class LootItemData {
        public final String id;
        public final String name;
        public final Formatting color;
        public final boolean combined;
        public final String dropMobId;
        public final String dropMobLsId;
        public final boolean isRarerDrop;

        public LootItemData(
                String id,
                String name,
                Formatting color,
                boolean combined,
                String dropMobId,
                String dropMobLsId,
                boolean isRarerDrop
        ) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.combined = combined;
            this.dropMobId = dropMobId;
            this.dropMobLsId = dropMobLsId;
            this.isRarerDrop = isRarerDrop;
        }
    }

    public static OverlayTextLine createClickableTextLine(
            String text,
            String hoverText,
            String defaultText,
            Runnable onClick,
            Runnable onMouseEnter,
            Runnable onMouseLeave,
            boolean lineBreak
    ) {
        if (onClick == null) onClick = () -> {};
        OverlayTextLine line = new OverlayTextLine(text, true, lineBreak).onClick(onClick);

        Runnable enterAction = onMouseEnter != null
                ? onMouseEnter
                : () -> line.setText(hoverText != null ? hoverText : text + Formatting.UNDERLINE);

        Runnable leaveAction = onMouseLeave != null
                ? onMouseLeave
                : () -> line.setText(defaultText != null ? defaultText : text);

        line.onMouseEnter(enterAction);
        line.onMouseLeave(leaveAction);

        return line;
    }
}
