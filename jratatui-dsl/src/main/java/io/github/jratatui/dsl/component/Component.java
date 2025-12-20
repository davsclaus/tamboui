/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.component;

import io.github.jratatui.dsl.element.Element;
import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.dsl.event.EventResult;
import io.github.jratatui.dsl.focus.Focusable;
import io.github.jratatui.layout.Constraint;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.tui.event.KeyEvent;
import io.github.jratatui.tui.event.MouseEvent;

/**
 * Base class for stateful components with event handling.
 * <p>
 * Components can handle their own key and mouse events when focused.
 * State should be managed using regular instance fields in subclasses:
 *
 * <pre>{@code
 * public class CounterComponent extends Component {
 *     private int count = 0;  // State as instance field
 *
 *     @Override
 *     protected EventResult onKeyEvent(KeyEvent event) {
 *         if (Keys.isUp(event)) {
 *             count++;
 *             return EventResult.HANDLED;
 *         }
 *         return EventResult.UNHANDLED;
 *     }
 *
 *     @Override
 *     protected Element render() {
 *         return text("Count: " + count);
 *     }
 * }
 * }</pre>
 */
public abstract class Component implements Element, Focusable {

    private String componentId;
    private Constraint layoutConstraint;
    private ComponentContext context;
    private boolean mounted = false;

    /**
     * Sets the component ID.
     *
     * @param id the component ID
     * @return this component for chaining
     */
    public Component id(String id) {
        this.componentId = id;
        return this;
    }

    @Override
    public String id() {
        return componentId;
    }

    /**
     * Sets the layout constraint.
     *
     * @param constraint the constraint
     * @return this component for chaining
     */
    public Component constraint(Constraint constraint) {
        this.layoutConstraint = constraint;
        return this;
    }

    @Override
    public Constraint constraint() {
        return layoutConstraint;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    // Lifecycle methods

    /**
     * Called when the component is first mounted.
     * Override to perform initialization.
     */
    protected void onMount() {
    }

    /**
     * Called when the component is removed from the tree.
     * Override to perform cleanup.
     */
    protected void onUnmount() {
    }

    // Event handling

    /**
     * Handles key events when this component is focused.
     *
     * @param event the key event
     * @return HANDLED if the event was handled, UNHANDLED otherwise
     */
    protected EventResult onKeyEvent(KeyEvent event) {
        return EventResult.UNHANDLED;
    }

    /**
     * Handles mouse events within this component's area.
     *
     * @param event the mouse event
     * @return HANDLED if the event was handled, UNHANDLED otherwise
     */
    protected EventResult onMouseEvent(MouseEvent event) {
        return EventResult.UNHANDLED;
    }

    // Focus callbacks

    /**
     * Called when this component gains focus.
     */
    protected void onFocusGained() {
    }

    /**
     * Called when this component loses focus.
     */
    protected void onFocusLost() {
    }

    /**
     * Returns the component context providing access to focus state and app state.
     *
     * @return the component context
     */
    protected ComponentContext context() {
        return context;
    }

    // Rendering

    /**
     * Renders the component's content.
     * Subclasses must implement this to define the component's appearance.
     *
     * @return the element tree to render
     */
    protected abstract Element render();

    @Override
    public final void render(Frame frame, Rect area, RenderContext renderContext) {
        // Initialize context
        this.context = new ComponentContext(
            componentId,
            renderContext.focusManager().isFocused(componentId),
            renderContext
        );

        // Handle mount lifecycle
        if (!mounted) {
            mounted = true;
            onMount();
        }

        // Register in component tree
        renderContext.componentTree().register(componentId, this);
        renderContext.componentTree().setArea(componentId, area);

        // Register as focusable
        if (componentId != null) {
            renderContext.focusManager().registerFocusable(componentId, area);
        }

        // Render the component's content
        Element content = render();
        if (content != null) {
            content.render(frame, area, renderContext);
        }
    }

    /**
     * Called internally to handle key events.
     * Routes to onKeyEvent if the component is focused.
     */
    public EventResult handleKeyEvent(KeyEvent event, boolean focused) {
        if (focused) {
            return onKeyEvent(event);
        }
        return EventResult.UNHANDLED;
    }

    /**
     * Called internally to handle mouse events.
     */
    public EventResult handleMouseEvent(MouseEvent event) {
        return onMouseEvent(event);
    }

    /**
     * Called when focus state changes.
     */
    public void notifyFocusChange(boolean focused) {
        if (focused) {
            onFocusGained();
        } else {
            onFocusLost();
        }
    }
}
