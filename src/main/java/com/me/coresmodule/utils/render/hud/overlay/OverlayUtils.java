package com.me.coresmodule.utils.render.hud.overlay;

import net.minecraft.network.chat.Style;

public class OverlayUtils {
    public static class LootItemData {
        public final String id;
        public final String name;
        public final Style color;
        public final boolean combined;
        public final String dropMobId;
        public final String dropMobLsId;
        public final boolean isRarerDrop;

        public LootItemData(
                String id,
                String name,
                Style color,
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

    public static OverlayTextLine createClickableComponentLine(
            String text,
            String hoverComponent,
            String defaultComponent,
            Runnable onClick,
            Runnable onMouseEnter,
            Runnable onMouseLeave,
            boolean lineBreak
    ) {
        if (onClick == null) onClick = () -> {};
        OverlayTextLine line = new OverlayTextLine(text, true, lineBreak).onClick(onClick);

        Runnable enterAction = onMouseEnter != null
                ? onMouseEnter
                : () -> line.setText(hoverComponent != null ? hoverComponent : text + Style.EMPTY.withUnderlined(true));

        Runnable leaveAction = onMouseLeave != null
                ? onMouseLeave
                : () -> line.setText(defaultComponent != null ? defaultComponent : text);

        line.onMouseEnter(enterAction);
        line.onMouseLeave(leaveAction);

        return line;
    }
}
