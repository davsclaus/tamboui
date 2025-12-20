/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.demo;

import io.github.jratatui.dsl.element.Element;
import io.github.jratatui.dsl.event.EventResult;
import io.github.jratatui.style.Color;
import io.github.jratatui.tui.event.KeyEvent;

/**
 * Base class for panel content implementations.
 */
abstract class PanelContent {
    private final String title;
    private final int width;
    private final int height;
    private final Color color;

    protected PanelContent(String title, int width, int height, Color color) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    String title() { return title; }
    int width() { return width; }
    int height() { return height; }
    Color color() { return color; }

    abstract Element render(boolean focused);

    void onTick(long tick) {}

    EventResult handleKey(KeyEvent event) { return EventResult.UNHANDLED; }
}
