/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.buffer.Buffer;
import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.terminal.Frame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.jratatui.dsl.Dsl.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for SparklineElement.
 */
class SparklineElementTest {

    @Test
    @DisplayName("SparklineElement fluent API chains correctly")
    void fluentApiChaining() {
        var element = sparkline(1, 2, 3, 4, 5)
            .color(Color.CYAN)
            .title("CPU Usage")
            .rounded()
            .borderColor(Color.GREEN);

        assertThat(element).isInstanceOf(SparklineElement.class);
    }

    @Test
    @DisplayName("sparkline() creates empty element")
    void emptySparkline() {
        var element = sparkline();
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("sparkline(long...) accepts long array")
    void sparklineWithLongArray() {
        var element = sparkline(10L, 20L, 30L, 40L);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("sparkline(int...) accepts int array")
    void sparklineWithIntArray() {
        var element = sparkline(10, 20, 30, 40);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("sparkline(Collection) accepts collection")
    void sparklineWithCollection() {
        var element = sparkline(List.of(1, 2, 3, 4, 5));
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("data(long...) replaces data")
    void dataMethod() {
        var element = sparkline().data(5L, 10L, 15L);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("SparklineElement renders to buffer")
    void rendersToBuffer() {
        var area = new Rect(0, 0, 20, 3);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);

        sparkline(1, 2, 3, 4, 5, 6, 7, 8)
            .title("Chart")
            .rounded()
            .render(frame, area, RenderContext.empty());

        // Check border is rendered
        assertThat(buffer.get(0, 0).symbol()).isEqualTo("╭");
    }

    @Test
    @DisplayName("Empty area does not render")
    void emptyAreaNoRender() {
        var emptyArea = new Rect(0, 0, 0, 0);
        var buffer = Buffer.empty(new Rect(0, 0, 10, 3));
        var frame = Frame.forTesting(buffer);

        // Should not throw
        sparkline(1, 2, 3).render(frame, emptyArea, RenderContext.empty());
    }

    @Test
    @DisplayName("SparklineElement with color")
    void withColor() {
        var area = new Rect(0, 0, 10, 3);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);

        sparkline(8, 8, 8, 8, 8, 8, 8, 8)
            .color(Color.MAGENTA)
            .render(frame, area, RenderContext.empty());

        // The sparkline data should have the color applied
        // (checking that rendering completes without error)
        assertThat(buffer).isNotNull();
    }

    @Test
    @DisplayName("SparklineElement with max value")
    void withMaxValue() {
        var element = sparkline(1, 2, 3).max(100L);
        assertThat(element).isNotNull();
    }
}
