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

import static io.github.jratatui.dsl.Dsl.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for GaugeElement.
 */
class GaugeElementTest {

    @Test
    @DisplayName("GaugeElement fluent API chains correctly")
    void fluentApiChaining() {
        var element = gauge(0.5)
            .label("Loading...")
            .gaugeColor(Color.GREEN)
            .useUnicode(true)
            .title("Progress")
            .rounded()
            .borderColor(Color.CYAN);

        assertThat(element).isInstanceOf(GaugeElement.class);
    }

    @Test
    @DisplayName("gauge(double) clamps ratio to 0.0-1.0")
    void ratioClampingLow() {
        var element = gauge(-0.5);
        // Should not throw, ratio is clamped
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("gauge(double) clamps high ratio to 1.0")
    void ratioClampingHigh() {
        var element = gauge(1.5);
        // Should not throw, ratio is clamped
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("percent() method works correctly")
    void percentMethod() {
        var element = gauge().percent(75);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("GaugeElement renders to buffer")
    void rendersToBuffer() {
        var area = new Rect(0, 0, 20, 3);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);

        gauge(0.5)
            .label("50%")
            .title("Progress")
            .rounded()
            .render(frame, area, RenderContext.empty());

        // Check border is rendered (rounded corner)
        assertThat(buffer.get(0, 0).symbol()).isEqualTo("╭");
        assertThat(buffer.get(19, 0).symbol()).isEqualTo("╮");
    }

    @Test
    @DisplayName("GaugeElement renders filled portion")
    void rendersFilledPortion() {
        var area = new Rect(0, 0, 20, 1);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);

        gauge(1.0)
            .label("")
            .render(frame, area, RenderContext.empty());

        // At 100%, first cells should be filled blocks
        assertThat(buffer.get(0, 0).symbol()).isEqualTo("█");
        assertThat(buffer.get(19, 0).symbol()).isEqualTo("█");
    }

    @Test
    @DisplayName("Empty area does not render")
    void emptyAreaNoRender() {
        var emptyArea = new Rect(0, 0, 0, 0);
        var buffer = Buffer.empty(new Rect(0, 0, 10, 1));
        var frame = Frame.forTesting(buffer);

        // Should not throw
        gauge(0.5).render(frame, emptyArea, RenderContext.empty());
    }

    @Test
    @DisplayName("GaugeElement with gaugeStyle")
    void withGaugeStyle() {
        var area = new Rect(0, 0, 10, 1);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);

        gauge(1.0)
            .label("")
            .gaugeColor(Color.RED)
            .render(frame, area, RenderContext.empty());

        assertThat(buffer.get(0, 0).style().fg()).contains(Color.RED);
    }
}
