/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.block;

import io.github.jratatui.layout.Alignment;
import io.github.jratatui.text.Line;
import io.github.jratatui.text.Span;

/**
 * A title for a block, with optional alignment.
 */
public final class Title {

    private final Line content;
    private final Alignment alignment;

    public Title(Line content, Alignment alignment) {
        this.content = content;
        this.alignment = alignment;
    }

    public static Title from(String text) {
        return new Title(Line.from(text), Alignment.LEFT);
    }

    public static Title from(Span span) {
        return new Title(Line.from(span), Alignment.LEFT);
    }

    public static Title from(Line line) {
        return new Title(line, Alignment.LEFT);
    }

    public Title alignment(Alignment alignment) {
        return new Title(content, alignment);
    }

    public Title left() {
        return alignment(Alignment.LEFT);
    }

    public Title centered() {
        return alignment(Alignment.CENTER);
    }

    public Title right() {
        return alignment(Alignment.RIGHT);
    }

    public Line content() {
        return content;
    }

    public Alignment alignment() {
        return alignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Title)) {
            return false;
        }
        Title title = (Title) o;
        return content.equals(title.content) && alignment == title.alignment;
    }

    @Override
    public int hashCode() {
        int result = content.hashCode();
        result = 31 * result + alignment.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Title[content=%s, alignment=%s]", content, alignment);
    }
}
