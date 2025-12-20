/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl;

import io.github.jratatui.dsl.element.Element;
import io.github.jratatui.dsl.elements.BarChartElement;
import io.github.jratatui.dsl.elements.CalendarElement;
import io.github.jratatui.dsl.elements.CanvasElement;
import io.github.jratatui.dsl.elements.ChartElement;
import io.github.jratatui.dsl.elements.Column;
import io.github.jratatui.dsl.elements.GaugeElement;
import io.github.jratatui.dsl.elements.LazyElement;
import io.github.jratatui.dsl.elements.LineGaugeElement;
import io.github.jratatui.dsl.elements.ListElement;
import io.github.jratatui.dsl.elements.Panel;
import io.github.jratatui.dsl.elements.Row;
import io.github.jratatui.dsl.elements.ScrollbarElement;
import io.github.jratatui.dsl.elements.Spacer;
import io.github.jratatui.dsl.elements.SparklineElement;
import io.github.jratatui.dsl.elements.TableElement;
import io.github.jratatui.dsl.elements.TabsElement;
import io.github.jratatui.dsl.elements.TextElement;
import io.github.jratatui.dsl.elements.TextInputElement;
import io.github.jratatui.layout.Constraint;
import io.github.jratatui.widgets.input.TextInputState;
import io.github.jratatui.widgets.scrollbar.ScrollbarState;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Static factory methods for building UI elements with the DSL.
 * Import statically to use:
 * <pre>
 * import static io.github.jratatui.dsl.Dsl.*;
 *
 * panel("Title",
 *     text("Hello").bold().cyan(),
 *     row(
 *         text("Left"),
 *         spacer(),
 *         text("Right")
 *     )
 * ).rounded()
 * </pre>
 */
public final class Dsl {

    private Dsl() {
        // Prevent instantiation
    }

    // ==================== Text ====================

    /**
     * Creates a text element with the given content.
     */
    public static TextElement text(String content) {
        return new TextElement(content);
    }

    /**
     * Creates a text element from any value (uses toString).
     */
    public static TextElement text(Object value) {
        return new TextElement(value);
    }

    /**
     * Creates a text element with multiple values concatenated.
     */
    public static TextElement text(Object... values) {
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            sb.append(value != null ? value.toString() : "");
        }
        return new TextElement(sb.toString());
    }

    // ==================== Containers ====================

    /**
     * Creates a panel with a title and children.
     */
    public static Panel panel(String title, Element... children) {
        return new Panel(title, children);
    }

    /**
     * Creates a panel without a title.
     */
    public static Panel panel(Element... children) {
        return new Panel(children);
    }

    /**
     * Creates an empty panel.
     */
    public static Panel panel() {
        return new Panel();
    }

    /**
     * Creates a horizontal row layout.
     */
    public static Row row(Element... children) {
        return new Row(children);
    }

    /**
     * Creates an empty row.
     */
    public static Row row() {
        return new Row();
    }

    /**
     * Creates a vertical column layout.
     */
    public static Column column(Element... children) {
        return new Column(children);
    }

    /**
     * Creates an empty column.
     */
    public static Column column() {
        return new Column();
    }

    // ==================== Spacer ====================

    /**
     * Creates a spacer that fills available space.
     */
    public static Spacer spacer() {
        return Spacer.fill();
    }

    /**
     * Creates a spacer with a fixed length.
     */
    public static Spacer spacer(int length) {
        return Spacer.length(length);
    }

    // ==================== Constraints ====================

    /**
     * Creates a length constraint.
     */
    public static Constraint length(int value) {
        return Constraint.length(value);
    }

    /**
     * Creates a percentage constraint.
     */
    public static Constraint percent(int value) {
        return Constraint.percentage(value);
    }

    /**
     * Creates a fill constraint with weight 1.
     */
    public static Constraint fill() {
        return Constraint.fill();
    }

    /**
     * Creates a fill constraint with the given weight.
     */
    public static Constraint fill(int weight) {
        return Constraint.fill(weight);
    }

    /**
     * Creates a minimum constraint.
     */
    public static Constraint min(int value) {
        return Constraint.min(value);
    }

    /**
     * Creates a maximum constraint.
     */
    public static Constraint max(int value) {
        return Constraint.max(value);
    }

    /**
     * Creates a ratio constraint.
     */
    public static Constraint ratio(int numerator, int denominator) {
        return Constraint.ratio(numerator, denominator);
    }

    // ==================== Lazy Elements ====================

    /**
     * Creates a lazy element that evaluates the supplier on each render.
     * <p>
     * This allows state to be captured in the closure:
     * <pre>{@code
     * int count = 0;
     * lazy(() -> text("Count: " + count))
     * }</pre>
     */
    public static Element lazy(Supplier<? extends Element> supplier) {
        return new LazyElement(supplier);
    }

    /**
     * Creates a panel with a title and lazy content.
     * The content supplier is evaluated on each render.
     * <pre>{@code
     * panel("Counter", () -> text("Count: " + count))
     * }</pre>
     */
    public static Panel panel(String title, Supplier<? extends Element> contentSupplier) {
        return new Panel(title, new LazyElement(contentSupplier));
    }

    /**
     * Creates a panel with lazy content (no title).
     */
    public static Panel panel(Supplier<? extends Element> contentSupplier) {
        return new Panel(new LazyElement(contentSupplier));
    }

    /**
     * Creates a row with lazy content.
     */
    public static Row row(Supplier<? extends Element> contentSupplier) {
        return new Row(new LazyElement(contentSupplier));
    }

    /**
     * Creates a column with lazy content.
     */
    public static Column column(Supplier<? extends Element> contentSupplier) {
        return new Column(new LazyElement(contentSupplier));
    }

    // ==================== Gauge ====================

    /**
     * Creates a gauge with the given ratio (0.0-1.0).
     */
    public static GaugeElement gauge(double ratio) {
        return new GaugeElement(ratio);
    }

    /**
     * Creates a gauge with the given percentage (0-100).
     */
    public static GaugeElement gauge(int percent) {
        return new GaugeElement(percent);
    }

    /**
     * Creates an empty gauge.
     */
    public static GaugeElement gauge() {
        return new GaugeElement();
    }

    // ==================== Line Gauge ====================

    /**
     * Creates a line gauge with the given ratio (0.0-1.0).
     */
    public static LineGaugeElement lineGauge(double ratio) {
        return new LineGaugeElement(ratio);
    }

    /**
     * Creates a line gauge with the given percentage (0-100).
     */
    public static LineGaugeElement lineGauge(int percent) {
        return new LineGaugeElement(percent);
    }

    /**
     * Creates an empty line gauge.
     */
    public static LineGaugeElement lineGauge() {
        return new LineGaugeElement();
    }

    // ==================== Sparkline ====================

    /**
     * Creates a sparkline with the given data values.
     */
    public static SparklineElement sparkline(long... data) {
        return new SparklineElement(data);
    }

    /**
     * Creates a sparkline with the given data values.
     */
    public static SparklineElement sparkline(int... data) {
        return new SparklineElement(data);
    }

    /**
     * Creates a sparkline with the given data values.
     */
    public static SparklineElement sparkline(Collection<? extends Number> data) {
        return new SparklineElement(data);
    }

    /**
     * Creates an empty sparkline.
     */
    public static SparklineElement sparkline() {
        return new SparklineElement();
    }

    // ==================== List ====================

    /**
     * Creates a list with the given items.
     */
    public static ListElement list(String... items) {
        return new ListElement(items);
    }

    /**
     * Creates a list with the given items.
     */
    public static ListElement list(List<String> items) {
        return new ListElement(items);
    }

    /**
     * Creates an empty list.
     */
    public static ListElement list() {
        return new ListElement();
    }

    // ==================== Table ====================

    /**
     * Creates an empty table.
     */
    public static TableElement table() {
        return new TableElement();
    }

    // ==================== Tabs ====================

    /**
     * Creates tabs with the given titles.
     */
    public static TabsElement tabs(String... titles) {
        return new TabsElement(titles);
    }

    /**
     * Creates tabs with the given titles.
     */
    public static TabsElement tabs(List<String> titles) {
        return new TabsElement(titles);
    }

    /**
     * Creates empty tabs.
     */
    public static TabsElement tabs() {
        return new TabsElement();
    }

    // ==================== Text Input ====================

    /**
     * Creates a text input with the given state.
     */
    public static TextInputElement textInput(TextInputState state) {
        return new TextInputElement(state);
    }

    /**
     * Creates a text input with a new state.
     */
    public static TextInputElement textInput() {
        return new TextInputElement();
    }

    // ==================== Bar Chart ====================

    /**
     * Creates a bar chart with the given values.
     */
    public static BarChartElement barChart(long... values) {
        return new BarChartElement().data(values);
    }

    /**
     * Creates an empty bar chart.
     */
    public static BarChartElement barChart() {
        return new BarChartElement();
    }

    // ==================== Chart ====================

    /**
     * Creates an empty chart.
     */
    public static ChartElement chart() {
        return new ChartElement();
    }

    // ==================== Canvas ====================

    /**
     * Creates a canvas with the given bounds.
     */
    public static CanvasElement canvas(double xMin, double xMax, double yMin, double yMax) {
        return new CanvasElement().bounds(xMin, xMax, yMin, yMax);
    }

    /**
     * Creates a canvas with default bounds (0.0-1.0).
     */
    public static CanvasElement canvas() {
        return new CanvasElement();
    }

    // ==================== Calendar ====================

    /**
     * Creates a calendar showing the given date's month.
     */
    public static CalendarElement calendar(LocalDate date) {
        return new CalendarElement(date);
    }

    /**
     * Creates a calendar showing the current month.
     */
    public static CalendarElement calendar() {
        return new CalendarElement();
    }

    // ==================== Scrollbar ====================

    /**
     * Creates a scrollbar with the given state.
     */
    public static ScrollbarElement scrollbar(ScrollbarState state) {
        return new ScrollbarElement().state(state);
    }

    /**
     * Creates a scrollbar with the given parameters.
     */
    public static ScrollbarElement scrollbar(int contentLength, int viewportLength, int position) {
        return new ScrollbarElement().state(contentLength, viewportLength, position);
    }

    /**
     * Creates a scrollbar.
     */
    public static ScrollbarElement scrollbar() {
        return new ScrollbarElement();
    }
}
