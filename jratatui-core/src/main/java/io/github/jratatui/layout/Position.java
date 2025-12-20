/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.layout;

/**
 * A position in 2D space, representing x and y coordinates.
 */
public final class Position {

    public static final Position ORIGIN = new Position(0, 0);

    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public Position offset(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position that = (Position) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(x);
        result = 31 * result + Integer.hashCode(y);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Position[x=%d, y=%d]", x, y);
    }
}
