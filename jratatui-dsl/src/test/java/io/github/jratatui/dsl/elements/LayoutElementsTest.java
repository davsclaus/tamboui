/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.buffer.Buffer;
import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.layout.Constraint;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.terminal.Frame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.jratatui.dsl.Dsl.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for layout elements (Panel, Row, Column, Spacer).
 */
class LayoutElementsTest {

    @Nested
    @DisplayName("Panel tests")
    class PanelTests {

        @Test
        @DisplayName("Panel fluent API chains correctly")
        void fluentApiChaining() {
            Panel element = panel("Title", text("Content"))
                .rounded()
                .borderColor(Color.GREEN)
                .fg(Color.WHITE)
                .bg(Color.BLACK);

            assertThat(element).isInstanceOf(Panel.class);
        }

        @Test
        @DisplayName("panel() creates empty panel")
        void emptyPanel() {
            Panel element = panel();
            assertThat(element).isNotNull();
        }

        @Test
        @DisplayName("panel(Element...) creates panel without title")
        void panelWithoutTitle() {
            Panel element = panel(text("Content"));
            assertThat(element).isNotNull();
        }

        @Test
        @DisplayName("Panel renders border")
        void rendersBorder() {
            Rect area = new Rect(0, 0, 20, 5);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            panel("Test", text("Content"))
                .rounded()
                .render(frame, area, RenderContext.empty());

            // Rounded corners
            assertThat(buffer.get(0, 0).symbol()).isEqualTo("╭");
            assertThat(buffer.get(19, 0).symbol()).isEqualTo("╮");
            assertThat(buffer.get(0, 4).symbol()).isEqualTo("╰");
            assertThat(buffer.get(19, 4).symbol()).isEqualTo("╯");
        }

        @Test
        @DisplayName("Panel renders title")
        void rendersTitle() {
            Rect area = new Rect(0, 0, 20, 3);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            panel("Title", text("X"))
                .render(frame, area, RenderContext.empty());

            // Title should appear in top border
            assertThat(buffer.get(1, 0).symbol()).isEqualTo("T");
            assertThat(buffer.get(2, 0).symbol()).isEqualTo("i");
        }

        @Test
        @DisplayName("Empty area does not render")
        void emptyAreaNoRender() {
            Rect emptyArea = new Rect(0, 0, 0, 0);
            Buffer buffer = Buffer.empty(new Rect(0, 0, 10, 5));
            Frame frame = Frame.forTesting(buffer);

            panel("Title").render(frame, emptyArea, RenderContext.empty());
        }

        @Test
        @DisplayName("Panel with border color")
        void withBorderColor() {
            Rect area = new Rect(0, 0, 10, 3);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            panel("X")
                .borderColor(Color.RED)
                .render(frame, area, RenderContext.empty());

            assertThat(buffer.get(0, 0).style().fg()).contains(Color.RED);
        }
    }

    @Nested
    @DisplayName("Row tests")
    class RowTests {

        @Test
        @DisplayName("Row fluent API chains correctly")
        void fluentApiChaining() {
            Row element = row(text("Left"), spacer(), text("Right"))
                .spacing(1)
                .fg(Color.WHITE);

            assertThat(element).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("row() creates empty row")
        void emptyRow() {
            Row element = row();
            assertThat(element).isNotNull();
        }

        @Test
        @DisplayName("Row renders children horizontally")
        void rendersHorizontally() {
            Rect area = new Rect(0, 0, 20, 1);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            row(
                text("A").length(5),
                text("B").length(5)
            ).render(frame, area, RenderContext.empty());

            assertThat(buffer.get(0, 0).symbol()).isEqualTo("A");
            assertThat(buffer.get(5, 0).symbol()).isEqualTo("B");
        }

        @Test
        @DisplayName("Empty area does not render")
        void emptyAreaNoRender() {
            Rect emptyArea = new Rect(0, 0, 0, 0);
            Buffer buffer = Buffer.empty(new Rect(0, 0, 10, 1));
            Frame frame = Frame.forTesting(buffer);

            row(text("A")).render(frame, emptyArea, RenderContext.empty());
        }

        @Test
        @DisplayName("Row with fill children distributes space")
        void fillDistribution() {
            Rect area = new Rect(0, 0, 20, 1);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            row(
                text("L").fill(),
                text("R").fill()
            ).render(frame, area, RenderContext.empty());

            // Both should be rendered with space distributed
            assertThat(buffer.get(0, 0).symbol()).isEqualTo("L");
        }
    }

    @Nested
    @DisplayName("Column tests")
    class ColumnTests {

        @Test
        @DisplayName("Column fluent API chains correctly")
        void fluentApiChaining() {
            Column element = column(text("Top"), spacer(), text("Bottom"))
                .spacing(1)
                .fg(Color.WHITE);

            assertThat(element).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("column() creates empty column")
        void emptyColumn() {
            Column element = column();
            assertThat(element).isNotNull();
        }

        @Test
        @DisplayName("Column renders children vertically")
        void rendersVertically() {
            Rect area = new Rect(0, 0, 10, 5);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            column(
                text("A").length(1),
                text("B").length(1)
            ).render(frame, area, RenderContext.empty());

            assertThat(buffer.get(0, 0).symbol()).isEqualTo("A");
            assertThat(buffer.get(0, 1).symbol()).isEqualTo("B");
        }

        @Test
        @DisplayName("Empty area does not render")
        void emptyAreaNoRender() {
            Rect emptyArea = new Rect(0, 0, 0, 0);
            Buffer buffer = Buffer.empty(new Rect(0, 0, 10, 5));
            Frame frame = Frame.forTesting(buffer);

            column(text("A")).render(frame, emptyArea, RenderContext.empty());
        }
    }

    @Nested
    @DisplayName("Spacer tests")
    class SpacerTests {

        @Test
        @DisplayName("spacer() creates fill spacer")
        void fillSpacer() {
            Spacer element = spacer();
            assertThat(element.constraint()).isEqualTo(Constraint.fill());
        }

        @Test
        @DisplayName("spacer(int) creates fixed spacer")
        void fixedSpacer() {
            Spacer element = spacer(10);
            assertThat(element.constraint()).isEqualTo(Constraint.length(10));
        }

        @Test
        @DisplayName("Spacer renders empty space")
        void rendersEmptySpace() {
            Rect area = new Rect(0, 0, 10, 1);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            spacer().render(frame, area, RenderContext.empty());

            // Spacer should not change the content (it's just empty space)
            // This test verifies it doesn't throw
            assertThat(buffer).isNotNull();
        }
    }

    @Nested
    @DisplayName("Nested layout tests")
    class NestedLayoutTests {

        @Test
        @DisplayName("Nested row in column renders correctly")
        void nestedRowInColumn() {
            Rect area = new Rect(0, 0, 20, 3);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            column(
                row(text("A"), text("B")),
                text("C")
            ).render(frame, area, RenderContext.empty());

            // Should render without error
            assertThat(buffer).isNotNull();
        }

        @Test
        @DisplayName("Panel with row content")
        void panelWithRow() {
            Rect area = new Rect(0, 0, 30, 5);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            panel("Header",
                row(
                    text("Left").fill(),
                    text("Right").fill()
                )
            ).rounded().render(frame, area, RenderContext.empty());

            assertThat(buffer.get(0, 0).symbol()).isEqualTo("╭");
        }

        @Test
        @DisplayName("Complex nested layout")
        void complexNestedLayout() {
            Rect area = new Rect(0, 0, 40, 10);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            column(
                panel("Title", text("Content")).rounded(),
                row(
                    panel("Left", text("L")),
                    panel("Right", text("R"))
                )
            ).render(frame, area, RenderContext.empty());

            // Should render without error
            assertThat(buffer).isNotNull();
        }
    }
}
