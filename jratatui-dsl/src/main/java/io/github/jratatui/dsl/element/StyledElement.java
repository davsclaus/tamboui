/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.element;

import io.github.jratatui.dsl.event.DragHandler;
import io.github.jratatui.dsl.event.EventResult;
import io.github.jratatui.dsl.event.KeyEventHandler;
import io.github.jratatui.dsl.event.MouseEventHandler;
import io.github.jratatui.layout.Constraint;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.style.Style;
import io.github.jratatui.tui.event.KeyEvent;
import io.github.jratatui.tui.event.MouseEvent;

/**
 * Abstract base for elements that support styling and event handling.
 * Provides a fluent API for setting colors, modifiers, and event handlers.
 *
 * @param <T> the concrete element type for method chaining
 */
public abstract class StyledElement<T extends StyledElement<T>> implements Element {

    protected Style style = Style.EMPTY;
    protected Constraint layoutConstraint;
    protected String elementId;
    protected KeyEventHandler keyHandler;
    protected MouseEventHandler mouseHandler;
    protected DragHandler dragHandler;
    protected boolean draggable;
    protected Rect lastRenderedArea;

    /**
     * Returns this element cast to the concrete type for method chaining.
     */
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    // Foreground colors

    public T fg(Color color) {
        this.style = style.fg(color);
        return self();
    }

    public T black() {
        return fg(Color.BLACK);
    }

    public T red() {
        return fg(Color.RED);
    }

    public T green() {
        return fg(Color.GREEN);
    }

    public T yellow() {
        return fg(Color.YELLOW);
    }

    public T blue() {
        return fg(Color.BLUE);
    }

    public T magenta() {
        return fg(Color.MAGENTA);
    }

    public T cyan() {
        return fg(Color.CYAN);
    }

    public T white() {
        return fg(Color.WHITE);
    }

    public T gray() {
        return fg(Color.GRAY);
    }

    // Background colors

    public T bg(Color color) {
        this.style = style.bg(color);
        return self();
    }

    public T onBlack() {
        return bg(Color.BLACK);
    }

    public T onRed() {
        return bg(Color.RED);
    }

    public T onGreen() {
        return bg(Color.GREEN);
    }

    public T onYellow() {
        return bg(Color.YELLOW);
    }

    public T onBlue() {
        return bg(Color.BLUE);
    }

    public T onMagenta() {
        return bg(Color.MAGENTA);
    }

    public T onCyan() {
        return bg(Color.CYAN);
    }

    public T onWhite() {
        return bg(Color.WHITE);
    }

    // Modifiers

    public T bold() {
        this.style = style.bold();
        return self();
    }

    public T dim() {
        this.style = style.dim();
        return self();
    }

    public T italic() {
        this.style = style.italic();
        return self();
    }

    public T underlined() {
        this.style = style.underlined();
        return self();
    }

    public T reversed() {
        this.style = style.reversed();
        return self();
    }

    public T crossedOut() {
        this.style = style.crossedOut();
        return self();
    }

    // Style

    public T style(Style style) {
        this.style = style;
        return self();
    }

    public Style getStyle() {
        return style;
    }

    // Layout constraint

    public T constraint(Constraint constraint) {
        this.layoutConstraint = constraint;
        return self();
    }

    public T length(int length) {
        return constraint(Constraint.length(length));
    }

    public T percent(int percent) {
        return constraint(Constraint.percentage(percent));
    }

    public T fill() {
        return constraint(Constraint.fill());
    }

    public T fill(int weight) {
        return constraint(Constraint.fill(weight));
    }

    public T min(int min) {
        return constraint(Constraint.min(min));
    }

    public T max(int max) {
        return constraint(Constraint.max(max));
    }

    @Override
    public Constraint constraint() {
        return layoutConstraint;
    }

    // ID for focus management

    public T id(String id) {
        this.elementId = id;
        return self();
    }

    @Override
    public String id() {
        return elementId;
    }

    // Event handlers

    /**
     * Sets the key event handler for this element.
     * The handler receives key events when this element is focused.
     *
     * @param handler the key event handler
     * @return this element for chaining
     */
    public T onKeyEvent(KeyEventHandler handler) {
        this.keyHandler = handler;
        return self();
    }

    /**
     * Sets the mouse event handler for this element.
     *
     * @param handler the mouse event handler
     * @return this element for chaining
     */
    public T onMouseEvent(MouseEventHandler handler) {
        this.mouseHandler = handler;
        return self();
    }

    /**
     * Makes this element draggable with the given handler.
     *
     * @param handler the drag handler
     * @return this element for chaining
     */
    public T onDrag(DragHandler handler) {
        this.dragHandler = handler;
        this.draggable = true;
        return self();
    }

    /**
     * Makes this element draggable with position callback.
     *
     * @param onMove callback receiving (deltaX, deltaY) during drag
     * @return this element for chaining
     */
    public T draggable(java.util.function.BiConsumer<Integer, Integer> onMove) {
        this.draggable = true;
        this.dragHandler = new DragHandler() {
            private int lastX, lastY;

            @Override
            public void onDragStart(int x, int y) {
                lastX = x;
                lastY = y;
            }

            @Override
            public void onDrag(int currentX, int currentY, int deltaX, int deltaY) {
                // Calculate incremental delta (since last drag event, not since start)
                int incrementalDeltaX = currentX - lastX;
                int incrementalDeltaY = currentY - lastY;
                lastX = currentX;
                lastY = currentY;
                onMove.accept(incrementalDeltaX, incrementalDeltaY);
            }

            @Override
            public void onDragEnd(int endX, int endY) {
            }
        };
        return self();
    }

    @Override
    public KeyEventHandler keyEventHandler() {
        return keyHandler;
    }

    @Override
    public MouseEventHandler mouseEventHandler() {
        return mouseHandler;
    }

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    @Override
    public Rect renderedArea() {
        return lastRenderedArea;
    }

    /**
     * Called by the render system to track the rendered area.
     */
    public void setRenderedArea(Rect area) {
        this.lastRenderedArea = area;
    }

    @Override
    public EventResult handleKeyEvent(KeyEvent event, boolean focused) {
        if (focused && keyHandler != null) {
            return keyHandler.handle(event);
        }
        return EventResult.UNHANDLED;
    }

    @Override
    public EventResult handleMouseEvent(MouseEvent event) {
        if (mouseHandler != null) {
            return mouseHandler.handle(event);
        }
        return EventResult.UNHANDLED;
    }

    /**
     * Returns the drag handler, if any.
     */
    public DragHandler dragHandler() {
        return dragHandler;
    }
}
