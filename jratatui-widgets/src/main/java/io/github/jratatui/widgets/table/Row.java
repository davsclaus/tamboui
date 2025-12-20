/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.table;

import io.github.jratatui.style.Style;
import static io.github.jratatui.util.CollectionUtil.listCopyOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A row in a {@link Table}.
 * <p>
 * A row is a collection of {@link Cell}s with optional styling and height configuration.
 *
 * <pre>{@code
 * // Simple row
 * Row.from(Cell.from("Name"), Cell.from("Age"))
 *
 * // Row from strings
 * Row.from("Alice", "30")
 *
 * // Styled row
 * Row.from("Header1", "Header2").style(Style.EMPTY.bold())
 *
 * // Row with custom height
 * Row.from("Tall", "Row").height(3)
 * }</pre>
 */
public final class Row {

    private final List<Cell> cells;
    private final Style style;
    private final int height;
    private final int bottomMargin;

    private Row(List<Cell> cells, Style style, int height, int bottomMargin) {
        this.cells = listCopyOf(cells);
        this.style = style;
        this.height = height;
        this.bottomMargin = bottomMargin;
    }

    /**
     * Creates a row from cells.
     */
    public static Row from(Cell... cells) {
        return new Row(Arrays.asList(cells), Style.EMPTY, 0, 0);
    }

    /**
     * Creates a row from cells.
     */
    public static Row from(List<Cell> cells) {
        return new Row(cells, Style.EMPTY, 0, 0);
    }

    /**
     * Creates a row from strings (each string becomes a cell).
     */
    public static Row from(String... contents) {
        List<Cell> cells = new ArrayList<>(contents.length);
        for (String content : contents) {
            cells.add(Cell.from(content));
        }
        return new Row(cells, Style.EMPTY, 0, 0);
    }

    /**
     * Creates an empty row.
     */
    public static Row empty() {
        return new Row(listCopyOf(), Style.EMPTY, 0, 0);
    }

    /**
     * Returns the cells in this row.
     */
    public List<Cell> cells() {
        return cells;
    }

    /**
     * Returns the style of this row.
     */
    public Style style() {
        return style;
    }

    /**
     * Returns the height of this row.
     */
    public int height() {
        // Use explicit height or calculate from cell content
        if (height > 0) {
            return height;
        }
        int maxHeight = 1;
        for (Cell cell : cells) {
            maxHeight = Math.max(maxHeight, cell.height());
        }
        return maxHeight;
    }

    /**
     * Returns the bottom margin of this row.
     */
    public int bottomMargin() {
        return bottomMargin;
    }

    /**
     * Returns a new row with the given style.
     */
    public Row style(Style style) {
        return new Row(this.cells, style, this.height, this.bottomMargin);
    }

    /**
     * Returns a new row with the given height.
     */
    public Row height(int height) {
        return new Row(this.cells, this.style, Math.max(1, height), this.bottomMargin);
    }

    /**
     * Returns a new row with the given bottom margin.
     */
    public Row bottomMargin(int margin) {
        return new Row(this.cells, this.style, this.height, Math.max(0, margin));
    }

    /**
     * Returns the total height including bottom margin.
     */
    public int totalHeight() {
        return height() + bottomMargin;
    }
}
