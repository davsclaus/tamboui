/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.dsl.element.StyledElement;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.style.Style;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.widgets.block.Block;
import io.github.jratatui.widgets.block.BorderType;
import io.github.jratatui.widgets.block.Borders;
import io.github.jratatui.widgets.block.Title;
import io.github.jratatui.widgets.list.ListItem;
import io.github.jratatui.widgets.list.ListState;
import io.github.jratatui.widgets.list.ListWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A DSL wrapper for the ListWidget.
 * <p>
 * Displays a selectable list of items.
 * <pre>{@code
 * list("Item 1", "Item 2", "Item 3")
 *     .state(listState)
 *     .highlightColor(Color.YELLOW)
 *     .title("My List")
 *     .rounded()
 * }</pre>
 */
public final class ListElement extends StyledElement<ListElement> {

    private final List<ListItem> items = new ArrayList<>();
    private ListState state;
    private Style highlightStyle = Style.EMPTY.reversed();
    private String highlightSymbol = ">> ";
    private String title;
    private BorderType borderType;
    private Color borderColor;

    public ListElement() {
    }

    public ListElement(String... items) {
        for (String item : items) {
            this.items.add(ListItem.from(item));
        }
    }

    public ListElement(List<String> items) {
        for (String item : items) {
            this.items.add(ListItem.from(item));
        }
    }

    /**
     * Sets the list items from strings.
     */
    public ListElement items(String... items) {
        this.items.clear();
        for (String item : items) {
            this.items.add(ListItem.from(item));
        }
        return this;
    }

    /**
     * Sets the list items from a collection.
     */
    public ListElement items(List<String> items) {
        this.items.clear();
        for (String item : items) {
            this.items.add(ListItem.from(item));
        }
        return this;
    }

    /**
     * Sets the list items from ListItem objects.
     */
    public ListElement listItems(ListItem... items) {
        this.items.clear();
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    /**
     * Adds an item to the list.
     */
    public ListElement add(String item) {
        this.items.add(ListItem.from(item));
        return this;
    }

    /**
     * Adds an item to the list.
     */
    public ListElement add(ListItem item) {
        this.items.add(item);
        return this;
    }

    /**
     * Sets the list state for selection tracking.
     */
    public ListElement state(ListState state) {
        this.state = state;
        return this;
    }

    /**
     * Sets the highlight style for selected items.
     */
    public ListElement highlightStyle(Style style) {
        this.highlightStyle = style;
        return this;
    }

    /**
     * Sets the highlight color for selected items.
     */
    public ListElement highlightColor(Color color) {
        this.highlightStyle = Style.EMPTY.fg(color).bold();
        return this;
    }

    /**
     * Sets the symbol displayed before the selected item.
     */
    public ListElement highlightSymbol(String symbol) {
        this.highlightSymbol = symbol;
        return this;
    }

    /**
     * Sets the title.
     */
    public ListElement title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Uses rounded borders.
     */
    public ListElement rounded() {
        this.borderType = BorderType.ROUNDED;
        return this;
    }

    /**
     * Sets the border color.
     */
    public ListElement borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        ListWidget.Builder builder = ListWidget.builder()
            .items(items)
            .style(style)
            .highlightStyle(highlightStyle)
            .highlightSymbol(highlightSymbol);

        if (title != null || borderType != null) {
            Block.Builder blockBuilder = Block.builder().borders(Borders.ALL);
            if (title != null) {
                blockBuilder.title(Title.from(title));
            }
            if (borderType != null) {
                blockBuilder.borderType(borderType);
            }
            if (borderColor != null) {
                blockBuilder.borderStyle(Style.EMPTY.fg(borderColor));
            }
            builder.block(blockBuilder.build());
        }

        ListWidget widget = builder.build();
        ListState effectiveState = state != null ? state : new ListState();
        frame.renderStatefulWidget(widget, area, effectiveState);
    }
}
