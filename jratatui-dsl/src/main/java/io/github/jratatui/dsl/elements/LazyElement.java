/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.dsl.element.Element;
import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.dsl.event.EventResult;
import io.github.jratatui.dsl.event.KeyEventHandler;
import io.github.jratatui.dsl.event.MouseEventHandler;
import io.github.jratatui.layout.Constraint;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.tui.event.KeyEvent;
import io.github.jratatui.tui.event.MouseEvent;

import java.util.function.Supplier;

/**
 * An element that lazily evaluates its content on each render.
 * <p>
 * This allows state to be captured in the supplier closure,
 * enabling encapsulated component-local state:
 * <pre>{@code
 * int count = 0;
 * panel("Counter", () -> text("Count: " + count))
 * }</pre>
 */
public final class LazyElement implements Element {

    private final Supplier<? extends Element> supplier;
    private Element lastElement;

    public LazyElement(Supplier<? extends Element> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        lastElement = supplier.get();
        if (lastElement != null) {
            lastElement.render(frame, area, context);
        }
    }

    @Override
    public Constraint constraint() {
        // Evaluate to get constraint if not yet rendered
        if (lastElement == null) {
            lastElement = supplier.get();
        }
        return lastElement != null ? lastElement.constraint() : null;
    }

    @Override
    public String id() {
        return lastElement != null ? lastElement.id() : null;
    }

    @Override
    public boolean isFocusable() {
        return lastElement != null && lastElement.isFocusable();
    }

    @Override
    public KeyEventHandler keyEventHandler() {
        return lastElement != null ? lastElement.keyEventHandler() : null;
    }

    @Override
    public MouseEventHandler mouseEventHandler() {
        return lastElement != null ? lastElement.mouseEventHandler() : null;
    }

    @Override
    public boolean isDraggable() {
        return lastElement != null && lastElement.isDraggable();
    }

    @Override
    public Rect renderedArea() {
        return lastElement != null ? lastElement.renderedArea() : null;
    }

    @Override
    public EventResult handleKeyEvent(KeyEvent event, boolean focused) {
        return lastElement != null ? lastElement.handleKeyEvent(event, focused) : EventResult.UNHANDLED;
    }

    @Override
    public EventResult handleMouseEvent(MouseEvent event) {
        return lastElement != null ? lastElement.handleMouseEvent(event) : EventResult.UNHANDLED;
    }
}
