/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.dsl.element.StyledElement;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.text.Line;
import io.github.jratatui.text.Span;
import io.github.jratatui.widgets.paragraph.Paragraph;

/**
 * A simple text element that displays styled text.
 */
public final class TextElement extends StyledElement<TextElement> {

    private final String content;

    public TextElement(String content) {
        this.content = content != null ? content : "";
    }

    public TextElement(Object value) {
        this.content = value != null ? String.valueOf(value) : "";
    }

    /**
     * Returns the text content.
     */
    public String content() {
        return content;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty() || content.isEmpty()) {
            return;
        }

        // Create a styled span and render as a paragraph
        Span span = Span.styled(content, style);
        Paragraph paragraph = Paragraph.from(Line.from(span));
        frame.renderWidget(paragraph, area);
    }
}
