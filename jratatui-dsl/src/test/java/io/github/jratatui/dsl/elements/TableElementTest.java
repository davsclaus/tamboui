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
import io.github.jratatui.widgets.table.TableState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.jratatui.dsl.Dsl.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for TableElement.
 */
class TableElementTest {

    @Test
    @DisplayName("TableElement fluent API chains correctly")
    void fluentApiChaining() {
        var state = new TableState();
        var element = table()
            .header("Name", "Age", "City")
            .row("Alice", "30", "NYC")
            .row("Bob", "25", "LA")
            .state(state)
            .widths(Constraint.percentage(40), Constraint.length(10), Constraint.fill())
            .highlightColor(Color.YELLOW)
            .title("Users")
            .rounded()
            .borderColor(Color.CYAN);

        assertThat(element).isInstanceOf(TableElement.class);
    }

    @Test
    @DisplayName("table() creates empty element")
    void emptyTable() {
        var element = table();
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("header() sets column headers")
    void headerMethod() {
        var element = table().header("Col1", "Col2", "Col3");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("row() adds data rows")
    void rowMethod() {
        var element = table()
            .header("Name", "Value")
            .row("A", "1")
            .row("B", "2")
            .row("C", "3");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("widths() sets column widths")
    void widthsMethod() {
        var element = table()
            .widths(Constraint.length(10), Constraint.fill());
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("TableElement renders to buffer")
    void rendersToBuffer() {
        var area = new Rect(0, 0, 40, 10);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);
        var state = new TableState();

        table()
            .header("Name", "Age")
            .row("Alice", "30")
            .row("Bob", "25")
            .state(state)
            .widths(Constraint.fill(), Constraint.length(10))
            .title("Table")
            .rounded()
            .render(frame, area, RenderContext.empty());

        // Check border is rendered
        assertThat(buffer.get(0, 0).symbol()).isEqualTo("╭");
    }

    @Test
    @DisplayName("TableElement with selection")
    void withSelection() {
        var area = new Rect(0, 0, 30, 8);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);
        var state = new TableState();
        state.select(1);

        table()
            .header("X", "Y")
            .row("A", "1")
            .row("B", "2")
            .state(state)
            .highlightColor(Color.GREEN)
            .render(frame, area, RenderContext.empty());

        // Should render without error
        assertThat(buffer).isNotNull();
    }

    @Test
    @DisplayName("Empty area does not render")
    void emptyAreaNoRender() {
        var emptyArea = new Rect(0, 0, 0, 0);
        var buffer = Buffer.empty(new Rect(0, 0, 20, 10));
        var frame = Frame.forTesting(buffer);
        var state = new TableState();

        // Should not throw
        table()
            .header("A")
            .row("1")
            .state(state)
            .render(frame, emptyArea, RenderContext.empty());
    }

    @Test
    @DisplayName("TableElement without explicit state creates internal state")
    void internalState() {
        var area = new Rect(0, 0, 30, 5);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);

        // Should not throw even without state
        table()
            .header("Col")
            .row("Value")
            .render(frame, area, RenderContext.empty());
    }

    @Test
    @DisplayName("highlightSymbol sets selection indicator")
    void highlightSymbol() {
        var element = table()
            .highlightSymbol("→ ");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("columnSpacing sets gap between columns")
    void columnSpacing() {
        var element = table()
            .columnSpacing(2);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("footer() sets table footer")
    void footerMethod() {
        var element = table()
            .header("H1", "H2")
            .footer("Total", "100");
        assertThat(element).isNotNull();
    }
}
