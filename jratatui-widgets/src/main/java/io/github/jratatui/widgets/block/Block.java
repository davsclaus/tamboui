/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.block;

import io.github.jratatui.buffer.Buffer;
import io.github.jratatui.buffer.Cell;
import io.github.jratatui.layout.Alignment;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Style;
import io.github.jratatui.widgets.Widget;

import java.util.EnumSet;
import java.util.Optional;

/**
 * A block is a container widget with optional borders and titles.
 */
public final class Block implements Widget {

    private final Optional<Title> title;
    private final Optional<Title> titleBottom;
    private final EnumSet<Borders> borders;
    private final BorderType borderType;
    private final Style borderStyle;
    private final Style style;
    private final Padding padding;

    private Block(Builder builder) {
        this.title = Optional.ofNullable(builder.title);
        this.titleBottom = Optional.ofNullable(builder.titleBottom);
        this.borders = builder.borders;
        this.borderType = builder.borderType;
        this.borderStyle = builder.borderStyle;
        this.style = builder.style;
        this.padding = builder.padding;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Block bordered() {
        return builder().borders(Borders.ALL).build();
    }

    public static Block empty() {
        return builder().build();
    }

    /**
     * Returns the inner area after accounting for borders and padding.
     */
    public Rect inner(Rect area) {
        int x = area.x();
        int y = area.y();
        int width = area.width();
        int height = area.height();

        // Account for borders
        if (borders.contains(Borders.LEFT)) {
            x += 1;
            width -= 1;
        }
        if (borders.contains(Borders.TOP)) {
            y += 1;
            height -= 1;
        }
        if (borders.contains(Borders.RIGHT)) {
            width -= 1;
        }
        if (borders.contains(Borders.BOTTOM)) {
            height -= 1;
        }

        // Account for padding
        x += padding.left();
        y += padding.top();
        width -= padding.horizontalTotal();
        height -= padding.verticalTotal();

        return new Rect(x, y, Math.max(0, width), Math.max(0, height));
    }

    @Override
    public void render(Rect area, Buffer buffer) {
        if (area.isEmpty()) {
            return;
        }

        // Fill background
        buffer.setStyle(area, style);

        // Draw borders
        if (!borders.isEmpty()) {
            renderBorders(area, buffer);
        }

        // Draw titles
        title.ifPresent(t -> renderTitle(t, area, buffer, true));
        titleBottom.ifPresent(t -> renderTitle(t, area, buffer, false));
    }

    private void renderBorders(Rect area, Buffer buffer) {
        BorderSet set = borderType.set();

        // Top border
        if (borders.contains(Borders.TOP) && area.height() > 0) {
            for (int x = area.left(); x < area.right(); x++) {
                buffer.set(x, area.top(), new Cell(set.horizontal(), borderStyle));
            }
        }

        // Bottom border
        if (borders.contains(Borders.BOTTOM) && area.height() > 1) {
            for (int x = area.left(); x < area.right(); x++) {
                buffer.set(x, area.bottom() - 1, new Cell(set.horizontal(), borderStyle));
            }
        }

        // Left border
        if (borders.contains(Borders.LEFT) && area.width() > 0) {
            for (int y = area.top(); y < area.bottom(); y++) {
                buffer.set(area.left(), y, new Cell(set.vertical(), borderStyle));
            }
        }

        // Right border
        if (borders.contains(Borders.RIGHT) && area.width() > 1) {
            for (int y = area.top(); y < area.bottom(); y++) {
                buffer.set(area.right() - 1, y, new Cell(set.vertical(), borderStyle));
            }
        }

        // Corners
        boolean hasTop = borders.contains(Borders.TOP);
        boolean hasBottom = borders.contains(Borders.BOTTOM);
        boolean hasLeft = borders.contains(Borders.LEFT);
        boolean hasRight = borders.contains(Borders.RIGHT);

        if (hasTop && hasLeft) {
            buffer.set(area.left(), area.top(), new Cell(set.topLeft(), borderStyle));
        }
        if (hasTop && hasRight && area.width() > 1) {
            buffer.set(area.right() - 1, area.top(), new Cell(set.topRight(), borderStyle));
        }
        if (hasBottom && hasLeft && area.height() > 1) {
            buffer.set(area.left(), area.bottom() - 1, new Cell(set.bottomLeft(), borderStyle));
        }
        if (hasBottom && hasRight && area.width() > 1 && area.height() > 1) {
            buffer.set(area.right() - 1, area.bottom() - 1, new Cell(set.bottomRight(), borderStyle));
        }
    }

    private void renderTitle(Title title, Rect area, Buffer buffer, boolean top) {
        int y = top ? area.top() : area.bottom() - 1;
        int availableWidth = area.width();

        if (borders.contains(Borders.LEFT)) {
            availableWidth -= 1;
        }
        if (borders.contains(Borders.RIGHT)) {
            availableWidth -= 1;
        }

        if (availableWidth <= 0) {
            return;
        }

        int titleWidth = Math.min(title.content().width(), availableWidth);
        int startX = area.left() + (borders.contains(Borders.LEFT) ? 1 : 0);

        // Calculate x position based on alignment
        int x;
        switch (title.alignment()) {
            case LEFT:
                x = startX;
                break;
            case CENTER:
                x = startX + (availableWidth - titleWidth) / 2;
                break;
            case RIGHT:
            default:
                x = startX + availableWidth - titleWidth;
                break;
        }

        buffer.setLine(x, y, title.content());
    }

    public static final class Builder {
        private Title title;
        private Title titleBottom;
        private EnumSet<Borders> borders = Borders.NONE;
        private BorderType borderType = BorderType.PLAIN;
        private Style borderStyle = Style.EMPTY;
        private Style style = Style.EMPTY;
        private Padding padding = Padding.NONE;

        private Builder() {}

        public Builder title(String title) {
            this.title = Title.from(title);
            return this;
        }

        public Builder title(Title title) {
            this.title = title;
            return this;
        }

        public Builder titleBottom(String title) {
            this.titleBottom = Title.from(title);
            return this;
        }

        public Builder titleBottom(Title title) {
            this.titleBottom = title;
            return this;
        }

        public Builder borders(EnumSet<Borders> borders) {
            this.borders = EnumSet.copyOf(borders);
            return this;
        }

        public Builder borderType(BorderType borderType) {
            this.borderType = borderType;
            return this;
        }

        public Builder borderStyle(Style borderStyle) {
            this.borderStyle = borderStyle;
            return this;
        }

        public Builder style(Style style) {
            this.style = style;
            return this;
        }

        public Builder padding(Padding padding) {
            this.padding = padding;
            return this;
        }

        public Builder padding(int value) {
            this.padding = Padding.uniform(value);
            return this;
        }

        public Block build() {
            return new Block(this);
        }
    }
}
