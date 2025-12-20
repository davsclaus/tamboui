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
import io.github.jratatui.widgets.chart.Axis;
import io.github.jratatui.widgets.chart.Chart;
import io.github.jratatui.widgets.chart.Dataset;
import io.github.jratatui.widgets.chart.LegendPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A DSL wrapper for the Chart widget.
 * <p>
 * Plots datasets in a cartesian coordinate system.
 * <pre>{@code
 * chart()
 *     .dataset(Dataset.builder()
 *         .name("Series 1")
 *         .data(new double[][] {{0,1}, {1,3}, {2,2}})
 *         .build())
 *     .xAxis(Axis.builder().bounds(0, 4).build())
 *     .yAxis(Axis.builder().bounds(0, 5).build())
 *     .title("My Chart")
 *     .rounded()
 * }</pre>
 */
public final class ChartElement extends StyledElement<ChartElement> {

    private final List<Dataset> datasets = new ArrayList<>();
    private Axis xAxis = Axis.defaults();
    private Axis yAxis = Axis.defaults();
    private LegendPosition legendPosition = LegendPosition.TOP_RIGHT;
    private String title;
    private BorderType borderType;
    private Color borderColor;

    public ChartElement() {
    }

    /**
     * Adds a dataset.
     */
    public ChartElement dataset(Dataset dataset) {
        this.datasets.add(dataset);
        return this;
    }

    /**
     * Sets all datasets.
     */
    public ChartElement datasets(Dataset... datasets) {
        this.datasets.clear();
        this.datasets.addAll(Arrays.asList(datasets));
        return this;
    }

    /**
     * Sets all datasets from a list.
     */
    public ChartElement datasets(List<Dataset> datasets) {
        this.datasets.clear();
        this.datasets.addAll(datasets);
        return this;
    }

    /**
     * Sets the X-axis configuration.
     */
    public ChartElement xAxis(Axis xAxis) {
        this.xAxis = xAxis != null ? xAxis : Axis.defaults();
        return this;
    }

    /**
     * Sets the Y-axis configuration.
     */
    public ChartElement yAxis(Axis yAxis) {
        this.yAxis = yAxis != null ? yAxis : Axis.defaults();
        return this;
    }

    /**
     * Sets both axis bounds.
     */
    public ChartElement bounds(double xMin, double xMax, double yMin, double yMax) {
        this.xAxis = Axis.builder().bounds(xMin, xMax).build();
        this.yAxis = Axis.builder().bounds(yMin, yMax).build();
        return this;
    }

    /**
     * Sets the legend position.
     */
    public ChartElement legendPosition(LegendPosition position) {
        this.legendPosition = position;
        return this;
    }

    /**
     * Hides the legend.
     */
    public ChartElement hideLegend() {
        this.legendPosition = null;
        return this;
    }

    /**
     * Sets the title.
     */
    public ChartElement title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Uses rounded borders.
     */
    public ChartElement rounded() {
        this.borderType = BorderType.ROUNDED;
        return this;
    }

    /**
     * Sets the border color.
     */
    public ChartElement borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        Chart.Builder builder = Chart.builder()
            .datasets(datasets)
            .xAxis(xAxis)
            .yAxis(yAxis)
            .style(style);

        if (legendPosition != null) {
            builder.legendPosition(legendPosition);
        } else {
            builder.hideLegend();
        }

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

        frame.renderWidget(builder.build(), area);
    }
}
