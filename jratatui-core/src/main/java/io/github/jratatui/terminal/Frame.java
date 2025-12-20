/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.terminal;

import io.github.jratatui.buffer.Buffer;
import io.github.jratatui.layout.Position;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.widgets.StatefulWidget;
import io.github.jratatui.widgets.Widget;

import java.util.Optional;

/**
 * A frame represents a single render cycle.
 * Widgets are rendered to the frame's buffer.
 */
public final class Frame {

    private final Buffer buffer;
    private final Rect area;
    private Position cursorPosition;
    private boolean cursorVisible;

    Frame(Buffer buffer) {
        this.buffer = buffer;
        this.area = buffer.area();
        this.cursorPosition = null;
        this.cursorVisible = false;
    }

    /**
     * Creates a frame for testing purposes.
     * This allows tests to create frames without going through Terminal.
     *
     * @param buffer the buffer to render to
     * @return a new frame backed by the given buffer
     */
    public static Frame forTesting(Buffer buffer) {
        return new Frame(buffer);
    }

    /**
     * Returns the area available for rendering.
     */
    public Rect area() {
        return area;
    }

    /**
     * Returns the underlying buffer.
     */
    public Buffer buffer() {
        return buffer;
    }

    /**
     * Returns the terminal width.
     */
    public int width() {
        return area.width();
    }

    /**
     * Returns the terminal height.
     */
    public int height() {
        return area.height();
    }

    /**
     * Renders a widget to the given area.
     */
    public void renderWidget(Widget widget, Rect area) {
        widget.render(area, buffer);
    }

    /**
     * Renders a stateful widget to the given area.
     */
    public <S> void renderStatefulWidget(StatefulWidget<S> widget, Rect area, S state) {
        widget.render(area, buffer, state);
    }

    /**
     * Sets the cursor position. The cursor will be shown at this position
     * after the frame is drawn.
     */
    public void setCursorPosition(Position position) {
        this.cursorPosition = position;
        this.cursorVisible = true;
    }

    /**
     * Sets the cursor position using x and y coordinates.
     */
    public void setCursorPosition(int x, int y) {
        setCursorPosition(new Position(x, y));
    }

    /**
     * Returns the cursor position if set.
     */
    Optional<Position> cursorPosition() {
        return Optional.ofNullable(cursorPosition);
    }

    /**
     * Returns whether the cursor should be visible.
     */
    boolean isCursorVisible() {
        return cursorVisible;
    }
}
