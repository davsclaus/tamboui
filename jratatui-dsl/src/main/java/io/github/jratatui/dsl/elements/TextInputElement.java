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
import io.github.jratatui.widgets.block.Block;
import io.github.jratatui.widgets.block.BorderType;
import io.github.jratatui.widgets.block.Borders;
import io.github.jratatui.widgets.block.Title;
import io.github.jratatui.widgets.input.TextInput;
import io.github.jratatui.widgets.input.TextInputState;

/**
 * A DSL wrapper for the TextInput widget.
 * <p>
 * A single-line text input field.
 * <pre>{@code
 * textInput(inputState)
 *     .placeholder("Enter name...")
 *     .title("Name")
 *     .rounded()
 * }</pre>
 */
public final class TextInputElement extends StyledElement<TextInputElement> {

    private TextInputState state;
    private Style cursorStyle = Style.EMPTY.reversed();
    private String placeholder = "";
    private Style placeholderStyle = Style.EMPTY.dim();
    private String title;
    private BorderType borderType;
    private Color borderColor;
    private boolean showCursor = true;

    public TextInputElement() {
        this.state = new TextInputState();
    }

    public TextInputElement(TextInputState state) {
        this.state = state != null ? state : new TextInputState();
    }

    /**
     * Sets the text input state.
     */
    public TextInputElement state(TextInputState state) {
        this.state = state != null ? state : new TextInputState();
        return this;
    }

    /**
     * Sets the initial text.
     */
    public TextInputElement text(String text) {
        if (state != null && text != null) {
            state.setText(text);
        }
        return this;
    }

    /**
     * Sets the placeholder text.
     */
    public TextInputElement placeholder(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
        return this;
    }

    /**
     * Sets the placeholder style.
     */
    public TextInputElement placeholderStyle(Style style) {
        this.placeholderStyle = style;
        return this;
    }

    /**
     * Sets the placeholder color.
     */
    public TextInputElement placeholderColor(Color color) {
        this.placeholderStyle = Style.EMPTY.fg(color);
        return this;
    }

    /**
     * Sets the cursor style.
     */
    public TextInputElement cursorStyle(Style style) {
        this.cursorStyle = style;
        return this;
    }

    /**
     * Sets whether to show the cursor.
     */
    public TextInputElement showCursor(boolean show) {
        this.showCursor = show;
        return this;
    }

    /**
     * Sets the title for the border.
     */
    public TextInputElement title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Uses rounded borders.
     */
    public TextInputElement rounded() {
        this.borderType = BorderType.ROUNDED;
        return this;
    }

    /**
     * Sets the border color.
     */
    public TextInputElement borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        TextInput.Builder builder = TextInput.builder()
            .style(style)
            .cursorStyle(cursorStyle)
            .placeholder(placeholder)
            .placeholderStyle(placeholderStyle);

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

        TextInput widget = builder.build();

        if (showCursor) {
            widget.renderWithCursor(area, frame.buffer(), state, frame);
        } else {
            frame.renderStatefulWidget(widget, area, state);
        }
    }
}
