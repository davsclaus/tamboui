/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.tui.event;

/**
 * Represents a mouse input event.
 */
public final class MouseEvent implements Event {

    private final MouseEventKind kind;
    private final MouseButton button;
    private final int x;
    private final int y;
    private final KeyModifiers modifiers;

    public MouseEvent(
        MouseEventKind kind,
        MouseButton button,
        int x,
        int y,
        KeyModifiers modifiers
    ) {
        this.kind = kind;
        this.button = button;
        this.x = x;
        this.y = y;
        this.modifiers = modifiers;
    }

    /**
     * Creates a mouse press event.
     */
    public static MouseEvent press(MouseButton button, int x, int y) {
        return new MouseEvent(MouseEventKind.PRESS, button, x, y, KeyModifiers.NONE);
    }

    /**
     * Creates a mouse release event.
     */
    public static MouseEvent release(MouseButton button, int x, int y) {
        return new MouseEvent(MouseEventKind.RELEASE, button, x, y, KeyModifiers.NONE);
    }

    /**
     * Creates a mouse move event.
     */
    public static MouseEvent move(int x, int y) {
        return new MouseEvent(MouseEventKind.MOVE, MouseButton.NONE, x, y, KeyModifiers.NONE);
    }

    /**
     * Creates a mouse drag event.
     */
    public static MouseEvent drag(MouseButton button, int x, int y) {
        return new MouseEvent(MouseEventKind.DRAG, button, x, y, KeyModifiers.NONE);
    }

    /**
     * Creates a scroll up event.
     */
    public static MouseEvent scrollUp(int x, int y) {
        return new MouseEvent(MouseEventKind.SCROLL_UP, MouseButton.NONE, x, y, KeyModifiers.NONE);
    }

    /**
     * Creates a scroll down event.
     */
    public static MouseEvent scrollDown(int x, int y) {
        return new MouseEvent(MouseEventKind.SCROLL_DOWN, MouseButton.NONE, x, y, KeyModifiers.NONE);
    }

    /**
     * Returns true if this is a press event.
     */
    public boolean isPress() {
        return kind == MouseEventKind.PRESS;
    }

    /**
     * Returns true if this is a release event.
     */
    public boolean isRelease() {
        return kind == MouseEventKind.RELEASE;
    }

    /**
     * Returns true if this is a left button event.
     */
    public boolean isLeftButton() {
        return button == MouseButton.LEFT;
    }

    /**
     * Returns true if this is a right button event.
     */
    public boolean isRightButton() {
        return button == MouseButton.RIGHT;
    }

    /**
     * Returns true if this is a scroll event.
     */
    public boolean isScroll() {
        return kind == MouseEventKind.SCROLL_UP || kind == MouseEventKind.SCROLL_DOWN;
    }

    public MouseEventKind kind() {
        return kind;
    }

    public MouseButton button() {
        return button;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public KeyModifiers modifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MouseEvent)) {
            return false;
        }
        MouseEvent that = (MouseEvent) o;
        return x == that.x
            && y == that.y
            && kind == that.kind
            && button == that.button
            && modifiers.equals(that.modifiers);
    }

    @Override
    public int hashCode() {
        int result = kind != null ? kind.hashCode() : 0;
        result = 31 * result + (button != null ? button.hashCode() : 0);
        result = 31 * result + Integer.hashCode(x);
        result = 31 * result + Integer.hashCode(y);
        result = 31 * result + modifiers.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format(
            "MouseEvent[kind=%s, button=%s, x=%d, y=%d, modifiers=%s]",
            kind,
            button,
            x,
            y,
            modifiers
        );
    }
}
