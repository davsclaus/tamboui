/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.block;

/**
 * Characters used to draw a border.
 */
public final class BorderSet {
    private final String horizontal;
    private final String vertical;
    private final String topLeft;
    private final String topRight;
    private final String bottomLeft;
    private final String bottomRight;

    public BorderSet(
        String horizontal,
        String vertical,
        String topLeft,
        String topRight,
        String bottomLeft,
        String bottomRight
    ) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public String horizontal() {
        return horizontal;
    }

    public String vertical() {
        return vertical;
    }

    public String topLeft() {
        return topLeft;
    }

    public String topRight() {
        return topRight;
    }

    public String bottomLeft() {
        return bottomLeft;
    }

    public String bottomRight() {
        return bottomRight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BorderSet)) {
            return false;
        }
        BorderSet that = (BorderSet) o;
        return horizontal.equals(that.horizontal)
            && vertical.equals(that.vertical)
            && topLeft.equals(that.topLeft)
            && topRight.equals(that.topRight)
            && bottomLeft.equals(that.bottomLeft)
            && bottomRight.equals(that.bottomRight);
    }

    @Override
    public int hashCode() {
        int result = horizontal.hashCode();
        result = 31 * result + vertical.hashCode();
        result = 31 * result + topLeft.hashCode();
        result = 31 * result + topRight.hashCode();
        result = 31 * result + bottomLeft.hashCode();
        result = 31 * result + bottomRight.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format(
            "BorderSet[horizontal=%s, vertical=%s, topLeft=%s, topRight=%s, bottomLeft=%s, bottomRight=%s]",
            horizontal, vertical, topLeft, topRight, bottomLeft, bottomRight);
    }
}
