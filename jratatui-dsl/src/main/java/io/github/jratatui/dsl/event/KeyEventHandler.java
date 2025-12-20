/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.event;

import io.github.jratatui.tui.event.KeyEvent;

/**
 * Handler for key events on an element.
 */
@FunctionalInterface
public interface KeyEventHandler {
    /**
     * Handles a key event.
     *
     * @param event the key event
     * @return HANDLED if the event was handled and should not propagate, UNHANDLED otherwise
     */
    EventResult handle(KeyEvent event);
}
