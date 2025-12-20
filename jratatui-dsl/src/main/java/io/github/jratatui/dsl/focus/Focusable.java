/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.focus;

/**
 * Interface for elements that can receive focus.
 */
public interface Focusable {

    /**
     * Returns whether this element can receive focus.
     *
     * @return true if focusable
     */
    boolean isFocusable();

    /**
     * Returns the unique ID of this focusable element.
     *
     * @return the element ID, or null if not set
     */
    String id();
}
