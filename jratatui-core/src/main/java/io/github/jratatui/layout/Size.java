/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.layout;

/**
 * A size representing width and height dimensions.
 */
public final class Size {

    public static final Size ZERO = new Size(0, 0);

    private final int width;
    private final int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int area() {
        return width * height;
    }

    public boolean isEmpty() {
        return width == 0 || height == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Size)) {
            return false;
        }
        Size size = (Size) o;
        return width == size.width && height == size.height;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(width);
        result = 31 * result + Integer.hashCode(height);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Size[width=%d, height=%d]", width, height);
    }
}
