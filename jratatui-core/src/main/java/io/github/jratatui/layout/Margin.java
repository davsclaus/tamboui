/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.layout;

/**
 * Margin values for top, right, bottom, and left sides.
 */
public final class Margin {

    public static final Margin NONE = new Margin(0, 0, 0, 0);

    private final int top;
    private final int right;
    private final int bottom;
    private final int left;

    public Margin(int top, int right, int bottom, int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public static Margin uniform(int value) {
        return new Margin(value, value, value, value);
    }

    public static Margin symmetric(int vertical, int horizontal) {
        return new Margin(vertical, horizontal, vertical, horizontal);
    }

    public static Margin horizontal(int value) {
        return new Margin(0, value, 0, value);
    }

    public static Margin vertical(int value) {
        return new Margin(value, 0, value, 0);
    }

    public int top() {
        return top;
    }

    public int right() {
        return right;
    }

    public int bottom() {
        return bottom;
    }

    public int left() {
        return left;
    }

    public int horizontalTotal() {
        return left + right;
    }

    public int verticalTotal() {
        return top + bottom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Margin)) {
            return false;
        }
        Margin margin = (Margin) o;
        return top == margin.top
            && right == margin.right
            && bottom == margin.bottom
            && left == margin.left;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(top);
        result = 31 * result + Integer.hashCode(right);
        result = 31 * result + Integer.hashCode(bottom);
        result = 31 * result + Integer.hashCode(left);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Margin[top=%d, right=%d, bottom=%d, left=%d]", top, right, bottom, left);
    }
}
