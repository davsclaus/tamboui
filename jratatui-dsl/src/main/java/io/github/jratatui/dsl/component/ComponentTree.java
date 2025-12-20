/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.component;

import io.github.jratatui.layout.Rect;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks the component hierarchy and their rendered areas.
 * Used for event routing and state management.
 */
public final class ComponentTree {

    private final Map<String, Component> components = new HashMap<>();
    private final Map<String, Rect> componentAreas = new HashMap<>();

    /**
     * Registers a component with its ID.
     *
     * @param id the component ID
     * @param component the component
     */
    public void register(String id, Component component) {
        if (id != null && component != null) {
            components.put(id, component);
        }
    }

    /**
     * Sets the rendered area for a component.
     *
     * @param id the component ID
     * @param area the rendered area
     */
    public void setArea(String id, Rect area) {
        if (id != null && area != null) {
            componentAreas.put(id, area);
        }
    }

    /**
     * Returns the component with the given ID.
     *
     * @param id the component ID
     * @return the component, or null if not found
     */
    public Component get(String id) {
        return components.get(id);
    }

    /**
     * Returns the rendered area for a component.
     *
     * @param id the component ID
     * @return the area, or null if not found
     */
    public Rect getArea(String id) {
        return componentAreas.get(id);
    }

    /**
     * Returns the component at the given screen position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the component, or null if none at that position
     */
    public Component componentAt(int x, int y) {
        for (Map.Entry<String, Rect> entry : componentAreas.entrySet()) {
            if (entry.getValue().contains(x, y)) {
                Component component = components.get(entry.getKey());
                if (component != null) {
                    return component;
                }
            }
        }
        return null;
    }

    /**
     * Clears all registered components and areas.
     * Should be called at the start of each render cycle.
     */
    public void clear() {
        componentAreas.clear();
    }

    /**
     * Removes a component by ID.
     *
     * @param id the component ID
     */
    public void remove(String id) {
        components.remove(id);
        componentAreas.remove(id);
    }

    /**
     * Returns all registered component IDs.
     *
     * @return the component IDs
     */
    public Iterable<String> componentIds() {
        return components.keySet();
    }
}
