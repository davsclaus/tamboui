/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.component;

import io.github.jratatui.dsl.element.RenderContext;

/**
 * Context available to components during rendering.
 * Provides access to focus state and the render context.
 */
public final class ComponentContext {

    private final String componentId;
    private final boolean focused;
    private final RenderContext renderContext;

    public ComponentContext(String componentId, boolean focused, RenderContext renderContext) {
        this.componentId = componentId;
        this.focused = focused;
        this.renderContext = renderContext;
    }

    /**
     * Returns the component's ID.
     *
     * @return the component ID
     */
    public String componentId() {
        return componentId;
    }

    /**
     * Returns whether this component is currently focused.
     *
     * @return true if focused
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * Returns the render context.
     *
     * @return the render context
     */
    public RenderContext renderContext() {
        return renderContext;
    }
}
