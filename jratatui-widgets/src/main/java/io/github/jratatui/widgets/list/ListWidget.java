/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.list;

import io.github.jratatui.buffer.Buffer;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Style;
import io.github.jratatui.text.Line;
import io.github.jratatui.widgets.StatefulWidget;
import io.github.jratatui.widgets.block.Block;
import static io.github.jratatui.util.CollectionUtil.listCopyOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A list widget for displaying selectable items.
 */
public final class ListWidget implements StatefulWidget<ListState> {

    private final List<ListItem> items;
    private final Optional<Block> block;
    private final Style style;
    private final Style highlightStyle;
    private final String highlightSymbol;

    private ListWidget(Builder builder) {
        this.items = listCopyOf(builder.items);
        this.block = Optional.ofNullable(builder.block);
        this.style = builder.style;
        this.highlightStyle = builder.highlightStyle;
        this.highlightSymbol = builder.highlightSymbol;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<ListItem> items() {
        return items;
    }

    @Override
    public void render(Rect area, Buffer buffer, ListState state) {
        if (area.isEmpty()) {
            return;
        }

        // Apply background style
        buffer.setStyle(area, style);

        // Render block if present
        Rect listArea = area;
        if (block.isPresent()) {
            block.get().render(area, buffer);
            listArea = block.get().inner(area);
        }

        if (listArea.isEmpty() || items.isEmpty()) {
            return;
        }

        // Ensure selected item is visible
        if (state.selected() != null) {
            state.scrollToSelected(listArea.height(), items);
        }

        int symbolWidth = highlightSymbol.length();
        int contentWidth = listArea.width() - symbolWidth;
        if (contentWidth <= 0) {
            return;
        }

        int y = listArea.top();
        int offset = state.offset();
        int currentOffset = 0;

        for (int i = 0; i < items.size() && y < listArea.bottom(); i++) {
            ListItem item = items.get(i);
            int itemHeight = item.height();

            // Skip items before the visible area
            if (currentOffset + itemHeight <= offset) {
                currentOffset += itemHeight;
                continue;
            }

            // Calculate visible portion of this item
            int startLine = Math.max(0, offset - currentOffset);
            int visibleHeight = Math.min(itemHeight - startLine, listArea.bottom() - y);

            boolean isSelected = state.selected() != null && state.selected() == i;
            Style itemStyle = item.style().patch(isSelected ? highlightStyle : Style.EMPTY);

            // Draw highlight symbol
            if (isSelected && symbolWidth > 0) {
                buffer.setString(listArea.left(), y, highlightSymbol, highlightStyle);
            }

            // Draw item content
            int contentX = listArea.left() + symbolWidth;
            List<Line> lines = item.content().lines();

            for (int lineIdx = startLine; lineIdx < startLine + visibleHeight && lineIdx < lines.size(); lineIdx++) {
                Line line = lines.get(lineIdx);
                buffer.setLine(contentX, y, line.patchStyle(itemStyle));
                y++;
            }

            currentOffset += itemHeight;
        }
    }

    public static final class Builder {
        private List<ListItem> items = new ArrayList<>();
        private Block block;
        private Style style = Style.EMPTY;
        private Style highlightStyle = Style.EMPTY.reversed();
        private String highlightSymbol = ">> ";

        private Builder() {}

        public Builder items(List<ListItem> items) {
            this.items = new ArrayList<>(items);
            return this;
        }

        public Builder items(ListItem... items) {
            this.items = new ArrayList<>(Arrays.asList(items));
            return this;
        }

        public Builder addItem(ListItem item) {
            this.items.add(item);
            return this;
        }

        public Builder addItem(String text) {
            this.items.add(ListItem.from(text));
            return this;
        }

        public Builder block(Block block) {
            this.block = block;
            return this;
        }

        public Builder style(Style style) {
            this.style = style;
            return this;
        }

        public Builder highlightStyle(Style highlightStyle) {
            this.highlightStyle = highlightStyle;
            return this;
        }

        public Builder highlightSymbol(String highlightSymbol) {
            this.highlightSymbol = highlightSymbol;
            return this;
        }

        public ListWidget build() {
            return new ListWidget(this);
        }
    }
}
