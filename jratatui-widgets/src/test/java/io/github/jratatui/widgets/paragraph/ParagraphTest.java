/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.paragraph;

import io.github.jratatui.buffer.Buffer;
import io.github.jratatui.layout.Alignment;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.style.Style;
import io.github.jratatui.text.Text;
import io.github.jratatui.widgets.block.Block;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

class ParagraphTest {

    @Test
    @DisplayName("Paragraph renders text")
    void rendersText() {
        Paragraph paragraph = Paragraph.builder()
            .text(Text.from("Hello"))
            .build();
        Rect area = new Rect(0, 0, 10, 1);
        Buffer buffer = Buffer.empty(area);

        paragraph.render(area, buffer);

        assertThat(buffer.get(0, 0).symbol()).isEqualTo("H");
        assertThat(buffer.get(1, 0).symbol()).isEqualTo("e");
        assertThat(buffer.get(2, 0).symbol()).isEqualTo("l");
        assertThat(buffer.get(3, 0).symbol()).isEqualTo("l");
        assertThat(buffer.get(4, 0).symbol()).isEqualTo("o");
    }

    @Test
    @DisplayName("Paragraph with style applies to background")
    void withStyle() {
        Style style = Style.EMPTY.fg(Color.RED);
        Paragraph paragraph = Paragraph.builder()
            .text(Text.from("Hi"))
            .style(style)
            .build();
        Rect area = new Rect(0, 0, 10, 1);
        Buffer buffer = Buffer.empty(area);

        paragraph.render(area, buffer);

        // Style applies to area background - cells without text retain the style
        assertThat(buffer.get(5, 0).style().fg()).contains(Color.RED);
        // Text cells use the span's style
        assertThat(buffer.get(0, 0).symbol()).isEqualTo("H");
    }

    @Test
    @DisplayName("Paragraph with block")
    void withBlock() {
        Paragraph paragraph = Paragraph.builder()
            .text(Text.from("Hi"))
            .block(Block.bordered())
            .build();
        Rect area = new Rect(0, 0, 10, 3);
        Buffer buffer = Buffer.empty(area);

        paragraph.render(area, buffer);

        // Block borders (Plain border type is default)
        assertThat(buffer.get(0, 0).symbol()).isEqualTo("┌");
        // Text inside block
        assertThat(buffer.get(1, 1).symbol()).isEqualTo("H");
    }

    @Test
    @DisplayName("Paragraph with center alignment")
    void centerAlignment() {
        Paragraph paragraph = Paragraph.builder()
            .text(Text.from("Hi"))
            .alignment(Alignment.CENTER)
            .build();
        Rect area = new Rect(0, 0, 10, 1);
        Buffer buffer = Buffer.empty(area);

        paragraph.render(area, buffer);

        // "Hi" is 2 chars, centered in 10 chars = starts at position 4
        assertThat(buffer.get(4, 0).symbol()).isEqualTo("H");
        assertThat(buffer.get(5, 0).symbol()).isEqualTo("i");
    }

    @Test
    @DisplayName("Paragraph with right alignment")
    void rightAlignment() {
        Paragraph paragraph = Paragraph.builder()
            .text(Text.from("Hi"))
            .alignment(Alignment.RIGHT)
            .build();
        Rect area = new Rect(0, 0, 10, 1);
        Buffer buffer = Buffer.empty(area);

        paragraph.render(area, buffer);

        // "Hi" is 2 chars, right-aligned in 10 chars = starts at position 8
        assertThat(buffer.get(8, 0).symbol()).isEqualTo("H");
        assertThat(buffer.get(9, 0).symbol()).isEqualTo("i");
    }

    @Test
    @DisplayName("Paragraph with multi-line text")
    void multiLine() {
        Paragraph paragraph = Paragraph.builder()
            .text(Text.from("Line1\nLine2"))
            .build();
        Rect area = new Rect(0, 0, 10, 2);
        Buffer buffer = Buffer.empty(area);

        paragraph.render(area, buffer);

        assertThat(buffer.get(0, 0).symbol()).isEqualTo("L");
        assertThat(buffer.get(0, 1).symbol()).isEqualTo("L");
    }
}
