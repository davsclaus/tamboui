/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.layout;

import static io.github.jratatui.util.CollectionUtil.listCopyOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A layout defines how to split a rectangular area into smaller areas
 * based on constraints.
 */
public final class Layout {

    private final Direction direction;
    private final List<Constraint> constraints;
    private final Margin margin;
    private final int spacing;
    private final Flex flex;

    private Layout(Direction direction, List<Constraint> constraints,
                   Margin margin, int spacing, Flex flex) {
        this.direction = direction;
        this.constraints = listCopyOf(constraints);
        this.margin = margin;
        this.spacing = spacing;
        this.flex = flex;
    }

    public static Layout vertical() {
        return new Layout(Direction.VERTICAL, listCopyOf(), Margin.NONE, 0, Flex.START);
    }

    public static Layout horizontal() {
        return new Layout(Direction.HORIZONTAL, listCopyOf(), Margin.NONE, 0, Flex.START);
    }

    public Layout constraints(Constraint... constraints) {
        return new Layout(direction, Arrays.asList(constraints), margin, spacing, flex);
    }

    public Layout constraints(List<Constraint> constraints) {
        return new Layout(direction, constraints, margin, spacing, flex);
    }

    public Layout margin(Margin margin) {
        return new Layout(direction, constraints, margin, spacing, flex);
    }

    public Layout margin(int value) {
        return new Layout(direction, constraints, Margin.uniform(value), spacing, flex);
    }

    public Layout spacing(int spacing) {
        return new Layout(direction, constraints, margin, spacing, flex);
    }

    public Layout flex(Flex flex) {
        return new Layout(direction, constraints, margin, spacing, flex);
    }

    public Direction direction() {
        return direction;
    }

    public List<Constraint> constraints() {
        return constraints;
    }

    public Margin margin() {
        return margin;
    }

    public int spacing() {
        return spacing;
    }

    public Flex flex() {
        return flex;
    }

    /**
     * Split the given area according to this layout's constraints.
     */
    public List<Rect> split(Rect area) {
        if (constraints.isEmpty()) {
            return listCopyOf();
        }

        // Apply margin first
        Rect inner = area.inner(margin);

        int available = direction == Direction.HORIZONTAL ? inner.width() : inner.height();
        int totalSpacing = spacing * (constraints.size() - 1);
        int distributable = Math.max(0, available - totalSpacing);

        int[] sizes = new int[constraints.size()];
        int[] mins = new int[constraints.size()];
        int[] maxs = new int[constraints.size()];
        boolean[] isFill = new boolean[constraints.size()];
        Arrays.fill(maxs, Integer.MAX_VALUE);

        int remaining = distributable;
        int fillWeight = 0;

        // First pass: calculate fixed sizes and collect fill weights
        for (int i = 0; i < constraints.size(); i++) {
            Constraint c = constraints.get(i);
            if (c instanceof Constraint.Length) {
                int v = ((Constraint.Length) c).value();
                sizes[i] = Math.min(v, distributable);
                remaining -= sizes[i];
            } else if (c instanceof Constraint.Percentage) {
                int p = ((Constraint.Percentage) c).value();
                sizes[i] = distributable * p / 100;
                remaining -= sizes[i];
            } else if (c instanceof Constraint.Ratio) {
                Constraint.Ratio ratio = (Constraint.Ratio) c;
                sizes[i] = distributable * ratio.numerator() / ratio.denominator();
                remaining -= sizes[i];
            } else if (c instanceof Constraint.Min) {
                int v = ((Constraint.Min) c).value();
                mins[i] = v;
                isFill[i] = true;
                fillWeight += 1;
            } else if (c instanceof Constraint.Max) {
                int v = ((Constraint.Max) c).value();
                maxs[i] = v;
                isFill[i] = true;
                fillWeight += 1;
            } else if (c instanceof Constraint.Fill) {
                int w = ((Constraint.Fill) c).weight();
                isFill[i] = true;
                fillWeight += w;
            }
        }

        // Second pass: distribute remaining space to Fill/Min/Max constraints
        if (fillWeight > 0 && remaining > 0) {
            for (int i = 0; i < constraints.size(); i++) {
                if (isFill[i]) {
                    Constraint c = constraints.get(i);
                    int weight;
                    if (c instanceof Constraint.Fill) {
                        weight = ((Constraint.Fill) c).weight();
                    } else if (c instanceof Constraint.Min || c instanceof Constraint.Max) {
                        weight = 1;
                    } else {
                        weight = 0;
                    }
                    sizes[i] = remaining * weight / fillWeight;
                }
            }
        }

        // Third pass: apply min/max bounds
        for (int i = 0; i < constraints.size(); i++) {
            sizes[i] = Math.max(mins[i], Math.min(maxs[i], sizes[i]));
        }

        // Build rectangles
        List<Rect> result = new ArrayList<>(constraints.size());
        int pos = direction == Direction.HORIZONTAL ? inner.x() : inner.y();

        for (int i = 0; i < sizes.length; i++) {
            Rect rect = direction == Direction.HORIZONTAL
                ? new Rect(pos, inner.y(), sizes[i], inner.height())
                : new Rect(inner.x(), pos, inner.width(), sizes[i]);
            result.add(rect);
            pos += sizes[i] + spacing;
        }

        return result;
    }
}
