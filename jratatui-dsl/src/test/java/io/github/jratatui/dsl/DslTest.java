/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl;

import io.github.jratatui.dsl.elements.BarChartElement;
import io.github.jratatui.dsl.elements.CalendarElement;
import io.github.jratatui.dsl.elements.CanvasElement;
import io.github.jratatui.dsl.elements.ChartElement;
import io.github.jratatui.dsl.elements.Column;
import io.github.jratatui.dsl.elements.GaugeElement;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static io.github.jratatui.dsl.Dsl.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for the Dsl factory methods.
 */
class DslTest {

    @Nested
    @DisplayName("Text factory methods")
    class TextFactoryTests {

        @Test
        @DisplayName("text(String) creates TextElement")
        void textWithString() {
            var element = text("Hello");
            assertThat(element).isInstanceOf(TextElement.class);
        }

        @Test
        @DisplayName("text(Object) creates TextElement with toString")
        void textWithObject() {
            var element = text(42);
            assertThat(element).isInstanceOf(TextElement.class);
        }

        @Test
        @DisplayName("text(Object...) concatenates values")
        void textWithMultipleValues() {
            var element = text("Count: ", 42, " items");
            assertThat(element).isInstanceOf(TextElement.class);
        }
    }

    @Nested
    @DisplayName("Container factory methods")
    class ContainerFactoryTests {

        @Test
        @DisplayName("panel() creates empty Panel")
        void emptyPanel() {
            var element = panel();
            assertThat(element).isInstanceOf(Panel.class);
        }

        @Test
        @DisplayName("panel(String, Element...) creates Panel with title and children")
        void panelWithTitleAndChildren() {
            var element = panel("Title", text("content"));
            assertThat(element).isInstanceOf(Panel.class);
        }

        @Test
        @DisplayName("row() creates empty Row")
        void emptyRow() {
            var element = row();
            assertThat(element).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("row(Element...) creates Row with children")
        void rowWithChildren() {
            var element = row(text("left"), text("right"));
            assertThat(element).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("column() creates empty Column")
        void emptyColumn() {
            var element = column();
            assertThat(element).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("column(Element...) creates Column with children")
        void columnWithChildren() {
            var element = column(text("top"), text("bottom"));
            assertThat(element).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("spacer() creates fill Spacer")
        void fillSpacer() {
            var element = spacer();
            assertThat(element).isInstanceOf(Spacer.class);
            assertThat(element.constraint()).isEqualTo(Constraint.fill());
        }

        @Test
        @DisplayName("spacer(int) creates fixed Spacer")
        void fixedSpacer() {
            var element = spacer(5);
            assertThat(element).isInstanceOf(Spacer.class);
            assertThat(element.constraint()).isEqualTo(Constraint.length(5));
        }
    }

    @Nested
    @DisplayName("Constraint factory methods")
    class ConstraintFactoryTests {

        @Test
        @DisplayName("length(int) creates length constraint")
        void lengthConstraint() {
            assertThat(length(10)).isEqualTo(Constraint.length(10));
        }

        @Test
        @DisplayName("percent(int) creates percentage constraint")
        void percentConstraint() {
            assertThat(percent(50)).isEqualTo(Constraint.percentage(50));
        }

        @Test
        @DisplayName("fill() creates fill constraint")
        void fillConstraint() {
            assertThat(fill()).isEqualTo(Constraint.fill());
        }

        @Test
        @DisplayName("fill(int) creates weighted fill constraint")
        void weightedFillConstraint() {
            assertThat(fill(2)).isEqualTo(Constraint.fill(2));
        }

        @Test
        @DisplayName("min(int) creates minimum constraint")
        void minConstraint() {
            assertThat(min(5)).isEqualTo(Constraint.min(5));
        }

        @Test
        @DisplayName("max(int) creates maximum constraint")
        void maxConstraint() {
            assertThat(max(20)).isEqualTo(Constraint.max(20));
        }

        @Test
        @DisplayName("ratio(int, int) creates ratio constraint")
        void ratioConstraint() {
            assertThat(ratio(1, 3)).isEqualTo(Constraint.ratio(1, 3));
        }
    }

    @Nested
    @DisplayName("Gauge factory methods")
    class GaugeFactoryTests {

        @Test
        @DisplayName("gauge() creates empty GaugeElement")
        void emptyGauge() {
            var element = gauge();
            assertThat(element).isInstanceOf(GaugeElement.class);
        }

        @Test
        @DisplayName("gauge(double) creates GaugeElement with ratio")
        void gaugeWithRatio() {
            var element = gauge(0.75);
            assertThat(element).isInstanceOf(GaugeElement.class);
        }

        @Test
        @DisplayName("gauge(int) creates GaugeElement with percent")
        void gaugeWithPercent() {
            var element = gauge(75);
            assertThat(element).isInstanceOf(GaugeElement.class);
        }
    }

    @Nested
    @DisplayName("LineGauge factory methods")
    class LineGaugeFactoryTests {

        @Test
        @DisplayName("lineGauge() creates empty LineGaugeElement")
        void emptyLineGauge() {
            var element = lineGauge();
            assertThat(element).isInstanceOf(LineGaugeElement.class);
        }

        @Test
        @DisplayName("lineGauge(double) creates LineGaugeElement with ratio")
        void lineGaugeWithRatio() {
            var element = lineGauge(0.5);
            assertThat(element).isInstanceOf(LineGaugeElement.class);
        }

        @Test
        @DisplayName("lineGauge(int) creates LineGaugeElement with percent")
        void lineGaugeWithPercent() {
            var element = lineGauge(50);
            assertThat(element).isInstanceOf(LineGaugeElement.class);
        }
    }

    @Nested
    @DisplayName("Sparkline factory methods")
    class SparklineFactoryTests {

        @Test
        @DisplayName("sparkline() creates empty SparklineElement")
        void emptySparkline() {
            var element = sparkline();
            assertThat(element).isInstanceOf(SparklineElement.class);
        }

        @Test
        @DisplayName("sparkline(long...) creates SparklineElement with data")
        void sparklineWithLongData() {
            var element = sparkline(1L, 2L, 3L, 4L, 5L);
            assertThat(element).isInstanceOf(SparklineElement.class);
        }

        @Test
        @DisplayName("sparkline(int...) creates SparklineElement with data")
        void sparklineWithIntData() {
            var element = sparkline(1, 2, 3, 4, 5);
            assertThat(element).isInstanceOf(SparklineElement.class);
        }

        @Test
        @DisplayName("sparkline(Collection) creates SparklineElement with data")
        void sparklineWithCollection() {
            var element = sparkline(List.of(1, 2, 3));
            assertThat(element).isInstanceOf(SparklineElement.class);
        }
    }

    @Nested
    @DisplayName("List factory methods")
    class ListFactoryTests {

        @Test
        @DisplayName("list() creates empty ListElement")
        void emptyList() {
            var element = list();
            assertThat(element).isInstanceOf(ListElement.class);
        }

        @Test
        @DisplayName("list(String...) creates ListElement with items")
        void listWithItems() {
            var element = list("Item 1", "Item 2", "Item 3");
            assertThat(element).isInstanceOf(ListElement.class);
        }

        @Test
        @DisplayName("list(List<String>) creates ListElement with items")
        void listWithItemsList() {
            var element = list(List.of("A", "B", "C"));
            assertThat(element).isInstanceOf(ListElement.class);
        }
    }

    @Nested
    @DisplayName("Table factory methods")
    class TableFactoryTests {

        @Test
        @DisplayName("table() creates empty TableElement")
        void emptyTable() {
            var element = table();
            assertThat(element).isInstanceOf(TableElement.class);
        }
    }

    @Nested
    @DisplayName("Tabs factory methods")
    class TabsFactoryTests {

        @Test
        @DisplayName("tabs() creates empty TabsElement")
        void emptyTabs() {
            var element = tabs();
            assertThat(element).isInstanceOf(TabsElement.class);
        }

        @Test
        @DisplayName("tabs(String...) creates TabsElement with titles")
        void tabsWithTitles() {
            var element = tabs("Home", "Settings", "About");
            assertThat(element).isInstanceOf(TabsElement.class);
        }

        @Test
        @DisplayName("tabs(List<String>) creates TabsElement with titles")
        void tabsWithTitlesList() {
            var element = tabs(List.of("Tab 1", "Tab 2"));
            assertThat(element).isInstanceOf(TabsElement.class);
        }
    }

    @Nested
    @DisplayName("TextInput factory methods")
    class TextInputFactoryTests {

        @Test
        @DisplayName("textInput() creates TextInputElement with new state")
        void emptyTextInput() {
            var element = textInput();
            assertThat(element).isInstanceOf(TextInputElement.class);
        }

        @Test
        @DisplayName("textInput(TextInputState) creates TextInputElement with state")
        void textInputWithState() {
            var state = new TextInputState();
            var element = textInput(state);
            assertThat(element).isInstanceOf(TextInputElement.class);
        }
    }

    @Nested
    @DisplayName("BarChart factory methods")
    class BarChartFactoryTests {

        @Test
        @DisplayName("barChart() creates empty BarChartElement")
        void emptyBarChart() {
            var element = barChart();
            assertThat(element).isInstanceOf(BarChartElement.class);
        }

        @Test
        @DisplayName("barChart(long...) creates BarChartElement with data")
        void barChartWithData() {
            var element = barChart(10L, 20L, 30L);
            assertThat(element).isInstanceOf(BarChartElement.class);
        }
    }

    @Nested
    @DisplayName("Chart factory methods")
    class ChartFactoryTests {

        @Test
        @DisplayName("chart() creates empty ChartElement")
        void emptyChart() {
            var element = chart();
            assertThat(element).isInstanceOf(ChartElement.class);
        }
    }

    @Nested
    @DisplayName("Canvas factory methods")
    class CanvasFactoryTests {

        @Test
        @DisplayName("canvas() creates CanvasElement with default bounds")
        void emptyCanvas() {
            var element = canvas();
            assertThat(element).isInstanceOf(CanvasElement.class);
        }

        @Test
        @DisplayName("canvas(bounds) creates CanvasElement with custom bounds")
        void canvasWithBounds() {
            var element = canvas(-10, 10, -10, 10);
            assertThat(element).isInstanceOf(CanvasElement.class);
        }
    }

    @Nested
    @DisplayName("Calendar factory methods")
    class CalendarFactoryTests {

        @Test
        @DisplayName("calendar() creates CalendarElement for current month")
        void calendarForCurrentMonth() {
            var element = calendar();
            assertThat(element).isInstanceOf(CalendarElement.class);
        }

        @Test
        @DisplayName("calendar(LocalDate) creates CalendarElement for specific month")
        void calendarForSpecificMonth() {
            var element = calendar(LocalDate.of(2025, 6, 15));
            assertThat(element).isInstanceOf(CalendarElement.class);
        }
    }

    @Nested
    @DisplayName("Scrollbar factory methods")
    class ScrollbarFactoryTests {

        @Test
        @DisplayName("scrollbar() creates ScrollbarElement")
        void emptyScrollbar() {
            var element = scrollbar();
            assertThat(element).isInstanceOf(ScrollbarElement.class);
        }

        @Test
        @DisplayName("scrollbar(ScrollbarState) creates ScrollbarElement with state")
        void scrollbarWithState() {
            var state = new ScrollbarState().contentLength(100);
            var element = scrollbar(state);
            assertThat(element).isInstanceOf(ScrollbarElement.class);
        }

        @Test
        @DisplayName("scrollbar(int, int, int) creates ScrollbarElement with parameters")
        void scrollbarWithParameters() {
            var element = scrollbar(100, 20, 50);
            assertThat(element).isInstanceOf(ScrollbarElement.class);
        }
    }

    @Nested
    @DisplayName("Lazy elements")
    class LazyElementTests {

        @Test
        @DisplayName("lazy(Supplier) creates lazy element")
        void lazyElement() {
            var element = lazy(() -> text("Dynamic"));
            assertThat(element).isNotNull();
        }

        @Test
        @DisplayName("panel with Supplier creates lazy content")
        void panelWithLazyContent() {
            int[] counter = {0};
            var element = panel("Counter", () -> text("Count: " + (++counter[0])));
            assertThat(element).isInstanceOf(Panel.class);
        }
    }
}
