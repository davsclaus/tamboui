/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.buffer;

import io.github.jratatui.layout.Position;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Style;
import io.github.jratatui.text.Line;
import io.github.jratatui.text.Span;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A buffer that stores cells for a rectangular area.
 * Widgets render to a Buffer, and the Terminal calculates diffs between buffers
 * to minimize updates sent to the backend.
 */
public final class Buffer {

    private final Rect area;
    private final Cell[] content;

    private Buffer(Rect area, Cell[] content) {
        this.area = area;
        this.content = content;
    }

    /**
     * Creates an empty buffer filled with empty cells.
     */
    public static Buffer empty(Rect area) {
        Cell[] content = new Cell[area.area()];
        Arrays.fill(content, Cell.EMPTY);
        return new Buffer(area, content);
    }

    /**
     * Creates a buffer filled with the given cell.
     */
    public static Buffer filled(Rect area, Cell cell) {
        Cell[] content = new Cell[area.area()];
        Arrays.fill(content, cell);
        return new Buffer(area, content);
    }

    public Rect area() {
        return area;
    }

    public int width() {
        return area.width();
    }

    public int height() {
        return area.height();
    }

    /**
     * Gets the cell at the given position.
     */
    public Cell get(int x, int y) {
        if (!area.contains(x, y)) {
            return Cell.EMPTY;
        }
        return content[index(x, y)];
    }

    /**
     * Gets the cell at the given position.
     */
    public Cell get(Position pos) {
        return get(pos.x(), pos.y());
    }

    /**
     * Sets the cell at the given position.
     */
    public void set(int x, int y, Cell cell) {
        if (area.contains(x, y)) {
            content[index(x, y)] = cell;
        }
    }

    /**
     * Sets the cell at the given position.
     */
    public void set(Position pos, Cell cell) {
        set(pos.x(), pos.y(), cell);
    }

    /**
     * Sets a string at the given position with the given style.
     * Returns the x position after the last character written.
     */
    public int setString(int x, int y, String string, Style style) {
        if (y < area.top() || y >= area.bottom()) {
            return x;
        }

        int col = x;
        for (int i = 0; i < string.length(); ) {
            if (col >= area.right()) {
                break;
            }

            int codePoint = string.codePointAt(i);
            String symbol = new String(Character.toChars(codePoint));

            if (col >= area.left()) {
                set(col, y, new Cell(symbol, style));
            }

            col++;
            i += Character.charCount(codePoint);
        }

        return col;
    }

    /**
     * Sets a span at the given position.
     * Returns the x position after the last character written.
     */
    public int setSpan(int x, int y, Span span) {
        return setString(x, y, span.content(), span.style());
    }

    /**
     * Sets a line at the given position.
     * Returns the x position after the last character written.
     */
    public int setLine(int x, int y, Line line) {
        int col = x;
        List<Span> spans = line.spans();
        for (int i = 0; i < spans.size(); i++) {
            col = setSpan(col, y, spans.get(i));
        }
        return col;
    }

    /**
     * Sets the style for all cells in the given area.
     */
    public void setStyle(Rect area, Style style) {
        Rect intersection = this.area.intersection(area);
        if (intersection.isEmpty()) {
            return;
        }

        for (int y = intersection.top(); y < intersection.bottom(); y++) {
            for (int x = intersection.left(); x < intersection.right(); x++) {
                Cell cell = get(x, y);
                set(x, y, cell.patchStyle(style));
            }
        }
    }

    /**
     * Fills the given area with the specified cell.
     */
    public void fill(Rect area, Cell cell) {
        Rect intersection = this.area.intersection(area);
        if (intersection.isEmpty()) {
            return;
        }

        for (int y = intersection.top(); y < intersection.bottom(); y++) {
            for (int x = intersection.left(); x < intersection.right(); x++) {
                set(x, y, cell);
            }
        }
    }

    /**
     * Clears the buffer, resetting all cells to empty.
     */
    public void clear() {
        Arrays.fill(content, Cell.EMPTY);
    }

    /**
     * Resets the buffer to empty cells within the given area.
     */
    public void clear(Rect area) {
        fill(area, Cell.EMPTY);
    }

    /**
     * Merges another buffer into this one at the specified position.
     */
    public void merge(Buffer other, int offsetX, int offsetY) {
        for (int y = 0; y < other.height(); y++) {
            for (int x = 0; x < other.width(); x++) {
                int destX = offsetX + x;
                int destY = offsetY + y;
                if (area.contains(destX, destY)) {
                    set(destX, destY, other.get(x + other.area.x(), y + other.area.y()));
                }
            }
        }
    }

    /**
     * Calculates the differences between this buffer and another.
     * Returns a list of cell updates needed to transform this buffer into the other.
     */
    public List<CellUpdate> diff(Buffer other) {
        List<CellUpdate> updates = new ArrayList<>();

        if (!this.area.equals(other.area)) {
            // If areas differ, return all cells from other as updates
            for (int y = other.area.top(); y < other.area.bottom(); y++) {
                for (int x = other.area.left(); x < other.area.right(); x++) {
                    updates.add(new CellUpdate(x, y, other.get(x, y)));
                }
            }
            return updates;
        }

        for (int i = 0; i < content.length; i++) {
            if (!content[i].equals(other.content[i])) {
                int x = area.x() + (i % area.width());
                int y = area.y() + (i / area.width());
                updates.add(new CellUpdate(x, y, other.content[i]));
            }
        }

        return updates;
    }

    private int index(int x, int y) {
        return (y - area.y()) * area.width() + (x - area.x());
    }
}
