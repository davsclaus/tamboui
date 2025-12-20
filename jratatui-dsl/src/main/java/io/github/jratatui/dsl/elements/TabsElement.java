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
import io.github.jratatui.widgets.tabs.Tabs;
import io.github.jratatui.widgets.tabs.TabsState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A DSL wrapper for the Tabs widget.
 * <p>
 * Displays a horizontal set of tabs with one selected.
 * <pre>{@code
 * tabs("Home", "Settings", "About")
 *     .state(tabsState)
 *     .highlightColor(Color.YELLOW)
 *     .divider(" | ")
 * }</pre>
 */
public final class TabsElement extends StyledElement<TabsElement> {

    private final List<String> titles = new ArrayList<>();
    private TabsState state;
    private Style highlightStyle = Style.EMPTY.reversed();
    private String divider = " | ";
    private String paddingLeft = "";
    private String paddingRight = "";
    private String title;
    private BorderType borderType;
    private Color borderColor;

    public TabsElement() {
    }

    public TabsElement(String... titles) {
        this.titles.addAll(Arrays.asList(titles));
    }

    public TabsElement(List<String> titles) {
        this.titles.addAll(titles);
    }

    /**
     * Sets the tab titles.
     */
    public TabsElement titles(String... titles) {
        this.titles.clear();
        this.titles.addAll(Arrays.asList(titles));
        return this;
    }

    /**
     * Sets the tab titles from a list.
     */
    public TabsElement titles(List<String> titles) {
        this.titles.clear();
        this.titles.addAll(titles);
        return this;
    }

    /**
     * Adds a tab.
     */
    public TabsElement add(String title) {
        this.titles.add(title);
        return this;
    }

    /**
     * Sets the tabs state for selection tracking.
     */
    public TabsElement state(TabsState state) {
        this.state = state;
        return this;
    }

    /**
     * Sets the selected tab index.
     */
    public TabsElement selected(int index) {
        this.state = new TabsState(index);
        return this;
    }

    /**
     * Sets the highlight style for the selected tab.
     */
    public TabsElement highlightStyle(Style style) {
        this.highlightStyle = style;
        return this;
    }

    /**
     * Sets the highlight color for the selected tab.
     */
    public TabsElement highlightColor(Color color) {
        this.highlightStyle = Style.EMPTY.fg(color).bold();
        return this;
    }

    /**
     * Sets the divider between tabs.
     */
    public TabsElement divider(String divider) {
        this.divider = divider;
        return this;
    }

    /**
     * Sets the padding around tab titles.
     */
    public TabsElement padding(String left, String right) {
        this.paddingLeft = left;
        this.paddingRight = right;
        return this;
    }

    /**
     * Sets the title for the border.
     */
    public TabsElement title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Uses rounded borders.
     */
    public TabsElement rounded() {
        this.borderType = BorderType.ROUNDED;
        return this;
    }

    /**
     * Sets the border color.
     */
    public TabsElement borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        Tabs.Builder builder = Tabs.builder()
            .titles(titles.toArray(new String[0]))
            .style(style)
            .highlightStyle(highlightStyle)
            .divider(divider)
            .padding(paddingLeft, paddingRight);

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

        Tabs widget = builder.build();
        TabsState effectiveState = state != null ? state : new TabsState(0);
        frame.renderStatefulWidget(widget, area, effectiveState);
    }
}
