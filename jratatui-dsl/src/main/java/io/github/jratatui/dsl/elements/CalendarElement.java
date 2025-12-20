/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.dsl.element.StyledElement;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.style.Style;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.widgets.block.Block;
import io.github.jratatui.widgets.block.BorderType;
import io.github.jratatui.widgets.block.Borders;
import io.github.jratatui.widgets.block.Title;
import io.github.jratatui.widgets.calendar.CalendarEventStore;
import io.github.jratatui.widgets.calendar.DateStyler;
import io.github.jratatui.widgets.calendar.Monthly;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * A DSL wrapper for the Monthly calendar widget.
 * <p>
 * Displays a calendar grid for a month.
 * <pre>{@code
 * calendar(LocalDate.now())
 *     .showMonthHeader()
 *     .showWeekdaysHeader()
 *     .highlightToday(Color.RED)
 *     .title("Calendar")
 *     .rounded()
 * }</pre>
 */
public final class CalendarElement extends StyledElement<CalendarElement> {

    private LocalDate displayDate = LocalDate.now();
    private DateStyler dateStyler = date -> Style.EMPTY;
    private Style monthHeaderStyle;
    private Style weekdaysHeaderStyle;
    private Style surroundingStyle;
    private Style defaultStyle;
    private DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
    private String title;
    private BorderType borderType;
    private Color borderColor;

    public CalendarElement() {
    }

    public CalendarElement(LocalDate date) {
        this.displayDate = date != null ? date : LocalDate.now();
    }

    /**
     * Sets the display date (determines which month to show).
     */
    public CalendarElement date(LocalDate date) {
        this.displayDate = date != null ? date : LocalDate.now();
        return this;
    }

    /**
     * Sets the date styler for customizing individual dates.
     */
    public CalendarElement dateStyler(DateStyler styler) {
        this.dateStyler = styler != null ? styler : date -> Style.EMPTY;
        return this;
    }

    /**
     * Highlights today with the given color.
     */
    public CalendarElement highlightToday(Color color) {
        this.dateStyler = CalendarEventStore.today(Style.EMPTY.fg(color).bold());
        return this;
    }

    /**
     * Highlights today with the given style.
     */
    public CalendarElement highlightToday(Style style) {
        this.dateStyler = CalendarEventStore.today(style);
        return this;
    }

    /**
     * Shows the month header with the given style.
     */
    public CalendarElement showMonthHeader(Style style) {
        this.monthHeaderStyle = style;
        return this;
    }

    /**
     * Shows the month header with bold style.
     */
    public CalendarElement showMonthHeader() {
        this.monthHeaderStyle = Style.EMPTY.bold();
        return this;
    }

    /**
     * Shows the weekdays header with the given style.
     */
    public CalendarElement showWeekdaysHeader(Style style) {
        this.weekdaysHeaderStyle = style;
        return this;
    }

    /**
     * Shows the weekdays header with cyan color.
     */
    public CalendarElement showWeekdaysHeader() {
        this.weekdaysHeaderStyle = Style.EMPTY.fg(Color.CYAN);
        return this;
    }

    /**
     * Shows surrounding days (from previous/next month) with the given style.
     */
    public CalendarElement showSurrounding(Style style) {
        this.surroundingStyle = style;
        return this;
    }

    /**
     * Shows surrounding days with dim style.
     */
    public CalendarElement showSurrounding() {
        this.surroundingStyle = Style.EMPTY.dim();
        return this;
    }

    /**
     * Sets the default date style.
     */
    public CalendarElement defaultStyle(Style style) {
        this.defaultStyle = style;
        return this;
    }

    /**
     * Sets the first day of the week.
     */
    public CalendarElement firstDayOfWeek(DayOfWeek day) {
        this.firstDayOfWeek = day != null ? day : DayOfWeek.MONDAY;
        return this;
    }

    /**
     * Sets the first day of week to Sunday.
     */
    public CalendarElement sundayFirst() {
        this.firstDayOfWeek = DayOfWeek.SUNDAY;
        return this;
    }

    /**
     * Sets the title.
     */
    public CalendarElement title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Uses rounded borders.
     */
    public CalendarElement rounded() {
        this.borderType = BorderType.ROUNDED;
        return this;
    }

    /**
     * Sets the border color.
     */
    public CalendarElement borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        Monthly.Builder builder = Monthly.builder(displayDate, dateStyler)
            .firstDayOfWeek(firstDayOfWeek);

        if (monthHeaderStyle != null) {
            builder.monthHeaderStyle(monthHeaderStyle);
        }

        if (weekdaysHeaderStyle != null) {
            builder.weekdaysHeaderStyle(weekdaysHeaderStyle);
        }

        if (surroundingStyle != null) {
            builder.surroundingStyle(surroundingStyle);
        }

        if (defaultStyle != null) {
            builder.defaultStyle(defaultStyle);
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

        frame.renderWidget(builder.build(), area);
    }
}
