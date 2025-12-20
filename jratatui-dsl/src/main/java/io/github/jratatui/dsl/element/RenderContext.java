/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.element;

import io.github.jratatui.dsl.event.EventRouter;
import io.github.jratatui.dsl.focus.FocusManager;
import io.github.jratatui.dsl.component.ComponentTree;
import io.github.jratatui.layout.Rect;

/**
 * Context provided during rendering, giving access to focus state, event routing, and component tree.
 */
public final class RenderContext {

    private final FocusManager focusManager;
    private final ComponentTree componentTree;
    private final EventRouter eventRouter;

    public RenderContext(FocusManager focusManager, ComponentTree componentTree, EventRouter eventRouter) {
        this.focusManager = focusManager;
        this.componentTree = componentTree;
        this.eventRouter = eventRouter;
    }

    /**
     * Creates an empty context for simple rendering without focus management.
     */
    public static RenderContext empty() {
        FocusManager fm = new FocusManager();
        return new RenderContext(fm, new ComponentTree(), new EventRouter(fm));
    }

    /**
     * Returns the focus manager.
     */
    public FocusManager focusManager() {
        return focusManager;
    }

    /**
     * Returns the component tree.
     */
    public ComponentTree componentTree() {
        return componentTree;
    }

    /**
     * Returns whether the element with the given ID is currently focused.
     *
     * @param elementId the element ID to check
     * @return true if focused, false otherwise
     */
    public boolean isFocused(String elementId) {
        return focusManager.isFocused(elementId);
    }

    /**
     * Returns whether any element is currently focused.
     *
     * @return true if an element is focused
     */
    public boolean hasFocus() {
        return focusManager.focusedId() != null;
    }

    /**
     * Returns the event router.
     */
    public EventRouter eventRouter() {
        return eventRouter;
    }

    /**
     * Registers an element for event routing.
     * Should be called during rendering to track elements.
     *
     * @param element the element to register
     * @param area the rendered area
     */
    public void registerElement(Element element, Rect area) {
        eventRouter.registerElement(element, area);
        if (element.isFocusable() && element.id() != null) {
            focusManager.registerFocusable(element.id(), area);
        }
    }
}
