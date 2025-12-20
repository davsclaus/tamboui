/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.dsl.element.Element;
import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.dsl.element.StyledElement;
import io.github.jratatui.layout.Constraint;
import io.github.jratatui.layout.Layout;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.style.Style;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.text.Line;
import io.github.jratatui.text.Span;
import io.github.jratatui.widgets.block.Block;
import io.github.jratatui.widgets.block.BorderType;
import io.github.jratatui.widgets.block.Borders;
import io.github.jratatui.widgets.block.Title;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A container element with borders and title.
 * Renders children vertically inside the bordered area.
 */
public final class Panel extends StyledElement<Panel> {

    private String title;
    private String bottomTitle;
    private BorderType borderType = BorderType.PLAIN;
    private Color borderColor;
    private Color focusedBorderColor;
    private final List<Element> children = new ArrayList<>();
    private boolean focusable;

    public Panel() {
    }

    public Panel(String title, Element... children) {
        this.title = title;
        this.children.addAll(Arrays.asList(children));
    }

    public Panel(Element... children) {
        this.children.addAll(Arrays.asList(children));
    }

    /**
     * Sets the panel title.
     */
    public Panel title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the bottom title.
     */
    public Panel bottomTitle(String title) {
        this.bottomTitle = title;
        return this;
    }

    /**
     * Sets the border type to rounded.
     */
    public Panel rounded() {
        this.borderType = BorderType.ROUNDED;
        return this;
    }

    /**
     * Sets the border type to double.
     */
    public Panel doubleBorder() {
        this.borderType = BorderType.DOUBLE;
        return this;
    }

    /**
     * Sets the border type to thick.
     */
    public Panel thick() {
        this.borderType = BorderType.THICK;
        return this;
    }

    /**
     * Sets the border type.
     */
    public Panel borderType(BorderType type) {
        this.borderType = type;
        return this;
    }

    /**
     * Sets the border color.
     */
    public Panel borderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    /**
     * Sets the border color when focused.
     */
    public Panel focusedBorderColor(Color color) {
        this.focusedBorderColor = color;
        return this;
    }

    /**
     * Makes this panel focusable.
     */
    public Panel focusable() {
        this.focusable = true;
        return this;
    }

    /**
     * Sets whether this panel is focusable.
     */
    public Panel focusable(boolean focusable) {
        this.focusable = focusable;
        return this;
    }

    /**
     * Adds a child element.
     */
    public Panel add(Element child) {
        this.children.add(child);
        return this;
    }

    /**
     * Adds multiple child elements.
     */
    public Panel add(Element... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    @Override
    public boolean isFocusable() {
        return focusable;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        // Track rendered area for event routing
        setRenderedArea(area);

        // Register this element for event routing
        context.registerElement(this, area);

        // Determine border color based on focus
        boolean isFocused = elementId != null && context.isFocused(elementId);
        Color effectiveBorderColor = isFocused && focusedBorderColor != null
            ? focusedBorderColor
            : borderColor;

        // Build the block
        Block.Builder blockBuilder = Block.builder()
            .borders(Borders.ALL)
            .borderType(borderType)
            .style(style);

        if (effectiveBorderColor != null) {
            blockBuilder.borderStyle(Style.EMPTY.fg(effectiveBorderColor));
        }

        if (title != null) {
            blockBuilder.title(Title.from(Line.from(Span.raw(title))));
        }

        if (bottomTitle != null) {
            blockBuilder.titleBottom(Title.from(bottomTitle));
        }

        Block block = blockBuilder.build();

        // Render the block
        frame.renderWidget(block, area);

        // Get inner area for children
        Rect innerArea = block.inner(area);
        if (innerArea.isEmpty() || children.isEmpty()) {
            return;
        }

        // Layout children vertically
        List<Constraint> constraints = new ArrayList<>();
        for (Element child : children) {
            Constraint c = child.constraint();
            // Default to fill() so children expand to use available space
            constraints.add(c != null ? c : Constraint.fill());
        }

        List<Rect> areas = Layout.vertical()
            .constraints(constraints.toArray(Constraint[]::new))
            .split(innerArea);

        // Render children and register them for events
        for (int i = 0; i < children.size() && i < areas.size(); i++) {
            Element child = children.get(i);
            Rect childArea = areas.get(i);
            child.render(frame, childArea, context);
            // Always register children - EventRouter will handle event dispatch
            context.registerElement(child, childArea);
        }
    }
}
