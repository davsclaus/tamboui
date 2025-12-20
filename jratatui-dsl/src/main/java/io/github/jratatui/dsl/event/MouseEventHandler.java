/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.event;

import io.github.jratatui.tui.event.MouseEvent;

/**
 * Handler for mouse events on an element.
 */
@FunctionalInterface
public interface MouseEventHandler {
    /**
     * Handles a mouse event.
     *
     * @param event the mouse event
     * @return HANDLED if the event was handled and should not propagate, UNHANDLED otherwise
     */
    EventResult handle(MouseEvent event);
}
