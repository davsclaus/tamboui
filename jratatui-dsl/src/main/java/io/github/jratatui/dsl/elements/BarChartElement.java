/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.dsl.element.StyledElement;
import io.github.jratatui.layout.Direction;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.style.Style;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.widgets.barchart.Bar;
import io.github.jratatui.widgets.barchart.BarChart;
import io.github.jratatui.widgets.barchart.BarGroup;
import io.github.jratatui.widgets.block.Block;
import io.github.jratatui.widgets.block.BorderType;
import io.github.jratatui.widgets.block.Borders;
import io.github.jratatui.widgets.block.Title;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A DSL wrapper for the BarChart widget.
 * <p>
 * Displays grouped bar charts.
 * <pre>{@code
 * barChart()
 *     .data(10, 20, 30, 40)
 *     .barWidth(3)
 *     .barColor(Color.CYAN)
 *     .title("Sales")
 *     .rounded()
 * }</pre>
 */
public final class BarChartElement extends StyledElement<BarChartElement> {

    private final List<BarGroup> groups = new ArrayList<>();
    private Long max;
    private int barWidth = 1;
    private int barGap = 1;
    private int groupGap = 1;
    private Direction direction = Direction.VERTICAL;
    private Style barStyle;
    private Style valueStyle;
    private Style labelStyle;
    private BarChart.BarSet barSet = BarChart.BarSet.NINE_LEVELS;
    private String title;
    private BorderType borderType;
    private Color borderColor;

    public BarChartElement() {
    }

    /**
     * Adds data as simple values (single group).
     */
    public BarChartElement data(long... values) {
        this.groups.clear();
        this.groups.add(BarGroup.of(values));
        return this;
    }

    /**
     * Adds data as bars with labels.
     */
    public BarChartElement data(Bar... bars) {
        this.groups.clear();
        this.groups.add(BarGroup.of(bars));
        return this;
    }

    /**
     * Adds a bar group.
     */
    public BarChartElement group(BarGroup group) {
        this.groups.add(group);
        return this;
    }

    /**
     * Adds bar groups.
     */
    public BarChartElement groups(BarGroup... groups) {
        this.groups.clear();
        this.groups.addAll(Arrays.asList(groups));
        return this;
    }

    /**
     * Adds bar groups from a list.
     */
    public BarChartElement groups(List<BarGroup> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
        return this;
    }

    /**
     * Sets the maximum value for scaling.
     */
    public BarChartElement max(long max) {
        this.max = max;
        return this;
    }

    /**
     * Uses auto-scaling based on data maximum.
     */
    public BarChartElement autoMax() {
        this.max = null;
        return this;
    }

    /**
     * Sets the bar width.
     */
    public BarChartElement barWidth(int width) {
        this.barWidth = Math.max(1, width);
        return this;
    }

    /**
     * Sets the gap between bars in a group.
     */
    public BarChartElement barGap(int gap) {
        this.barGap = Math.max(0, gap);
        return this;
    }

    /**
     * Sets the gap between groups.
     */
    public BarChartElement groupGap(int gap) {
        this.groupGap = Math.max(0, gap);
        return this;
    }

    /**
     * Sets horizontal direction.
     */
    public BarChartElement horizontal() {
        this.direction = Direction.HORIZONTAL;
        return this;
    }

    /**
     * Sets vertical direction.
     */
    public BarChartElement vertical() {
        this.direction = Direction.VERTICAL;
        return this;
    }

    /**
     * Sets the direction.
     */
    public BarChartElement direction(Direction direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Sets the bar style.
     */
    public BarChartElement barStyle(Style style) {
        this.barStyle = style;
        return this;
    }

    /**
     * Sets the bar color.
     */
    public BarChartElement barColor(Color color) {
        this.barStyle = Style.EMPTY.fg(color);
        return this;
    }

    /**
     * Sets the value display style.
     */
    public BarChartElement valueStyle(Style style) {
        this.valueStyle = style;
        return this;
    }

    /**
     * Sets the label style.
     */
    public BarChartElement labelStyle(Style style) {
        this.labelStyle = style;
        return this;
    }

    /**
     * Uses three-level bar set.
     */
    public BarChartElement threeLevels() {
        this.barSet = BarChart.BarSet.THREE_LEVELS;
        return this;
    }

    /**
     * Sets the bar character set.
     */
    public BarChartElement barSet(BarChart.BarSet barSet) {
        this.barSet = barSet;
        return this;
    }

    /**
     * Sets the title.
     */
    public BarChartElement title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Uses rounded borders.
     */
    public BarChartElement rounded() {
        this.borderType = BorderType.ROUNDED;
        return this;
    }

    /**
     * Sets the border color.
     */
    public BarChartElement borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        var builder = BarChart.builder()
            .data(groups)
            .barWidth(barWidth)
            .barGap(barGap)
            .groupGap(groupGap)
            .direction(direction)
            .barSet(barSet)
            .style(style);

        if (max != null) {
            builder.max(max);
        }

        if (barStyle != null) {
            builder.barStyle(barStyle);
        }

        if (valueStyle != null) {
            builder.valueStyle(valueStyle);
        }

        if (labelStyle != null) {
            builder.labelStyle(labelStyle);
        }

        if (title != null || borderType != null) {
            var blockBuilder = Block.builder().borders(Borders.ALL);
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

        frame.renderWidget(builder.build(), area);
    }
}
