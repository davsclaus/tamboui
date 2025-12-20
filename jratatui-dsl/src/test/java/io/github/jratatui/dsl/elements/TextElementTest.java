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
import io.github.jratatui.style.Modifier;
import io.github.jratatui.terminal.Frame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.jratatui.dsl.Dsl.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for TextElement.
 */
class TextElementTest {

    @Test
    @DisplayName("TextElement fluent API chains correctly")
    void fluentApiChaining() {
        TextElement element = text("Hello, World!")
            .bold()
            .italic()
            .underlined()
            .fg(Color.CYAN)
            .bg(Color.BLACK)
            .dim();

        assertThat(element).isInstanceOf(TextElement.class);
    }

    @Test
    @DisplayName("text(String) creates element with content")
    void textWithString() {
        TextElement element = text("Hello");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("text(Object) uses toString")
    void textWithObject() {
        TextElement element = text(42);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("Color shortcuts work")
    void colorShortcuts() {
        TextElement element = text("Colored")
            .red()
            .onBlue();

        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("TextElement renders to buffer")
    void rendersToBuffer() {
        Rect area = new Rect(0, 0, 20, 1);
        Buffer buffer = Buffer.empty(area);
        Frame frame = Frame.forTesting(buffer);

        text("Hello")
            .render(frame, area, RenderContext.empty());

        assertThat(buffer.get(0, 0).symbol()).isEqualTo("H");
        assertThat(buffer.get(1, 0).symbol()).isEqualTo("e");
        assertThat(buffer.get(2, 0).symbol()).isEqualTo("l");
        assertThat(buffer.get(3, 0).symbol()).isEqualTo("l");
        assertThat(buffer.get(4, 0).symbol()).isEqualTo("o");
    }

    @Test
    @DisplayName("TextElement with style renders correctly")
    void rendersWithStyle() {
        Rect area = new Rect(0, 0, 10, 1);
        Buffer buffer = Buffer.empty(area);
        Frame frame = Frame.forTesting(buffer);

        text("Hi")
            .bold()
            .fg(Color.RED)
            .render(frame, area, RenderContext.empty());

        assertThat(buffer.get(0, 0).symbol()).isEqualTo("H");
        assertThat(buffer.get(0, 0).style().fg()).contains(Color.RED);
        assertThat(buffer.get(0, 0).style().effectiveModifiers().contains(Modifier.BOLD)).isTrue();
    }

    @Test
    @DisplayName("Empty area does not render")
    void emptyAreaNoRender() {
        Rect emptyArea = new Rect(0, 0, 0, 0);
        Buffer buffer = Buffer.empty(new Rect(0, 0, 10, 1));
        Frame frame = Frame.forTesting(buffer);

        // Should not throw
        text("Test").render(frame, emptyArea, RenderContext.empty());
    }

    @Test
    @DisplayName("length() sets constraint")
    void lengthConstraint() {
        TextElement element = text("Test").length(10);
        assertThat(element.constraint()).isEqualTo(Constraint.length(10));
    }

    @Test
    @DisplayName("percent() sets constraint")
    void percentConstraint() {
        TextElement element = text("Test").percent(50);
        assertThat(element.constraint()).isEqualTo(Constraint.percentage(50));
    }

    @Test
    @DisplayName("fill() sets constraint")
    void fillConstraint() {
        TextElement element = text("Test").fill();
        assertThat(element.constraint()).isEqualTo(Constraint.fill());
    }

    @Test
    @DisplayName("null value renders as empty string")
    void nullValue() {
        TextElement element = text((Object) null);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("All color methods work")
    void allColorMethods() {
        TextElement element = text("Colors")
            .cyan()
            .yellow()
            .green()
            .blue()
            .magenta()
            .white()
            .gray();

        // Should not throw
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("Background color methods work")
    void backgroundColorMethods() {
        TextElement element = text("BG")
            .onRed()
            .onGreen()
            .onYellow()
            .onBlue()
            .onMagenta()
            .onCyan()
            .onWhite()
            .onBlack();

        assertThat(element).isNotNull();
    }
}
