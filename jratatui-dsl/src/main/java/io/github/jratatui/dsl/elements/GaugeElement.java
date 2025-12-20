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
import io.github.jratatui.text.Line;
import io.github.jratatui.widgets.block.Block;
import io.github.jratatui.widgets.block.BorderType;
import io.github.jratatui.widgets.block.Borders;
import io.github.jratatui.widgets.block.Title;
import io.github.jratatui.widgets.gauge.Gauge;

/**
 * A DSL wrapper for the Gauge widget.
 * <p>
 * Displays a progress bar filled according to the progress value.
 * <pre>{@code
 * gauge(0.75)
 *     .label("75% complete")
 *     .gaugeColor(Color.GREEN)
 *     .title("Progress")
 * }</pre>
 */
public final class GaugeElement extends StyledElement<GaugeElement> {

    private double ratio = 0.0;
    private String label;
    private Style gaugeStyle = Style.EMPTY;
    private boolean useUnicode = true;
    private String title;
    private BorderType borderType;
    private Color borderColor;

    public GaugeElement() {
    }

    public GaugeElement(double ratio) {
        this.ratio = Math.max(0.0, Math.min(1.0, ratio));
    }

    public GaugeElement(int percent) {
        this.ratio = Math.max(0, Math.min(100, percent)) / 100.0;
    }

    /**
     * Sets the progress as a ratio (0.0-1.0).
     */
    public GaugeElement ratio(double ratio) {
        this.ratio = Math.max(0.0, Math.min(1.0, ratio));
        return this;
    }

    /**
     * Sets the progress as a percentage (0-100).
     */
    public GaugeElement percent(int percent) {
        this.ratio = Math.max(0, Math.min(100, percent)) / 100.0;
        return this;
    }

    /**
     * Sets the label displayed in the gauge.
     */
    public GaugeElement label(String label) {
        this.label = label;
        return this;
    }

    /**
     * Sets the style for the filled portion.
     */
    public GaugeElement gaugeStyle(Style style) {
        this.gaugeStyle = style;
        return this;
    }

    /**
     * Sets the color for the filled portion.
     */
    public GaugeElement gaugeColor(Color color) {
        this.gaugeStyle = Style.EMPTY.fg(color);
        return this;
    }

    /**
     * Enables or disables unicode block characters.
     */
    public GaugeElement useUnicode(boolean useUnicode) {
        this.useUnicode = useUnicode;
        return this;
    }

    /**
     * Sets the title for the gauge's border.
     */
    public GaugeElement title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets rounded borders.
     */
    public GaugeElement rounded() {
        this.borderType = BorderType.ROUNDED;
        return this;
    }

    /**
     * Sets the border color.
     */
    public GaugeElement borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        Gauge.Builder builder = Gauge.builder()
            .ratio(ratio)
            .gaugeStyle(gaugeStyle)
            .style(style)
            .useUnicode(useUnicode);

        if (label != null) {
            builder.label(label);
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
