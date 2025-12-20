/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.dsl.element.StyledElement;
import io.github.jratatui.layout.Constraint;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.style.Style;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.widgets.block.Block;
import io.github.jratatui.widgets.block.BorderType;
import io.github.jratatui.widgets.block.Borders;
import io.github.jratatui.widgets.block.Title;
import io.github.jratatui.widgets.table.Row;
import io.github.jratatui.widgets.table.Table;
import io.github.jratatui.widgets.table.TableState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A DSL wrapper for the Table widget.
 * <p>
 * Displays data in rows and columns.
 * <pre>{@code
 * table()
 *     .header("Name", "Age", "City")
 *     .row("Alice", "30", "NYC")
 *     .row("Bob", "25", "LA")
 *     .widths(percent(40), length(10), fill())
 *     .state(tableState)
 *     .title("Users")
 *     .rounded()
 * }</pre>
 */
public final class TableElement extends StyledElement<TableElement> {

    private final List<Row> rows = new ArrayList<>();
    private final List<Constraint> widths = new ArrayList<>();
    private Row header;
    private Row footer;
    private TableState state;
    private Style highlightStyle = Style.EMPTY.reversed();
    private String highlightSymbol = ">> ";
    private int columnSpacing = 1;
    private String title;
    private BorderType borderType;
    private Color borderColor;

    public TableElement() {
    }

    /**
     * Sets the header row from strings.
     */
    public TableElement header(String... cells) {
        this.header = Row.from(cells).style(Style.EMPTY.bold());
        return this;
    }

    /**
     * Sets the header row.
     */
    public TableElement header(Row header) {
        this.header = header;
        return this;
    }

    /**
     * Sets the footer row from strings.
     */
    public TableElement footer(String... cells) {
        this.footer = Row.from(cells);
        return this;
    }

    /**
     * Sets the footer row.
     */
    public TableElement footer(Row footer) {
        this.footer = footer;
        return this;
    }

    /**
     * Adds a row from strings.
     */
    public TableElement row(String... cells) {
        this.rows.add(Row.from(cells));
        return this;
    }

    /**
     * Adds a row.
     */
    public TableElement row(Row row) {
        this.rows.add(row);
        return this;
    }

    /**
     * Sets all rows.
     */
    public TableElement rows(Row... rows) {
        this.rows.clear();
        this.rows.addAll(Arrays.asList(rows));
        return this;
    }

    /**
     * Sets all rows from a list.
     */
    public TableElement rows(List<Row> rows) {
        this.rows.clear();
        this.rows.addAll(rows);
        return this;
    }

    /**
     * Sets the column width constraints.
     */
    public TableElement widths(Constraint... widths) {
        this.widths.clear();
        this.widths.addAll(Arrays.asList(widths));
        return this;
    }

    /**
     * Sets the column width constraints from a list.
     */
    public TableElement widths(List<Constraint> widths) {
        this.widths.clear();
        this.widths.addAll(widths);
        return this;
    }

    /**
     * Sets the table state for selection tracking.
     */
    public TableElement state(TableState state) {
        this.state = state;
        return this;
    }

    /**
     * Sets the highlight style for selected rows.
     */
    public TableElement highlightStyle(Style style) {
        this.highlightStyle = style;
        return this;
    }

    /**
     * Sets the highlight color for selected rows.
     */
    public TableElement highlightColor(Color color) {
        this.highlightStyle = Style.EMPTY.fg(color).bold();
        return this;
    }

    /**
     * Sets the symbol displayed before the selected row.
     */
    public TableElement highlightSymbol(String symbol) {
        this.highlightSymbol = symbol;
        return this;
    }

    /**
     * Sets the spacing between columns.
     */
    public TableElement columnSpacing(int spacing) {
        this.columnSpacing = spacing;
        return this;
    }

    /**
     * Sets the title.
     */
    public TableElement title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Uses rounded borders.
     */
    public TableElement rounded() {
        this.borderType = BorderType.ROUNDED;
        return this;
    }

    /**
     * Sets the border color.
     */
    public TableElement borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        Table.Builder builder = Table.builder()
            .rows(rows)
            .widths(widths)
            .style(style)
            .highlightStyle(highlightStyle)
            .highlightSymbol(highlightSymbol)
            .columnSpacing(columnSpacing);

        if (header != null) {
            builder.header(header);
        }

        if (footer != null) {
            builder.footer(footer);
        }

        if (title != null || borderType != null) {
            Block.Builder blockBuilder = Block.builder().borders(Borders.ALL);
            if (title != null) {
                blockBuilder.title(Title.from(title));
            }
            if (borderType != null) {
                blockBuilder.borderType(borderType);
            }
            if (borderColor != null) {
                blockBuilder.borderStyle(Style.EMPTY.fg(borderColor));
            }
            builder.block(blockBuilder.build());
        }

        Table widget = builder.build();
        TableState effectiveState = state != null ? state : new TableState();
        frame.renderStatefulWidget(widget, area, effectiveState);
    }
}
