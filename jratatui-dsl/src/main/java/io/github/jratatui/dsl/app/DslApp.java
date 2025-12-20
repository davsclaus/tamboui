/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.app;

import io.github.jratatui.dsl.element.Element;
import io.github.jratatui.tui.TuiConfig;

/**
 * Base class for DSL-based TUI applications.
 * <p>
 * Extend this class to create a TUI application. State is managed
 * through instance fields, and events are handled by elements via
 * their {@code onKeyEvent()} and {@code onMouseEvent()} handlers.
 *
 * <pre>{@code
 * import static io.github.jratatui.dsl.Dsl.*;
 *
 * public class CounterApp extends DslApp {
 *     private int count = 0;
 *
 *     @Override
 *     protected Element render() {
 *         return panel("Counter",
 *             text("Count: " + count).bold().cyan(),
 *             text("[j/k] to change").dim()
 *         )
 *         .rounded()
 *         .id("main")
 *         .focusable()
 *         .onKeyEvent(event -> {
 *             if (Keys.isUp(event)) {
 *                 count++;
 *                 return EventResult.HANDLED;
 *             }
 *             if (Keys.isDown(event)) {
 *                 count--;
 *                 return EventResult.HANDLED;
 *             }
 *             return EventResult.UNHANDLED;
 *         });
 *     }
 *
 *     public static void main(String[] args) throws Exception {
 *         new CounterApp().run();
 *     }
 * }
 * }</pre>
 */
public abstract class DslApp {

    private DslRunner runner;

    /**
     * Returns the TUI configuration for this application.
     * Override to customize.
     *
     * @return the configuration
     */
    protected TuiConfig configure() {
        return TuiConfig.defaults();
    }

    /**
     * Renders the application UI.
     * Called each frame to get the current state.
     * <p>
     * Add event handlers to elements using {@code onKeyEvent()} and
     * {@code onMouseEvent()} methods.
     *
     * @return the root element to render
     */
    protected abstract Element render();

    /**
     * Called when the application starts, before the first render.
     * Override to perform initialization.
     */
    protected void onStart() {
    }

    /**
     * Called when the application is about to stop.
     * Override to perform cleanup.
     */
    protected void onStop() {
    }

    /**
     * Runs the application.
     *
     * @throws Exception if an error occurs
     */
    public void run() throws Exception {
        try (DslRunner r = DslRunner.create(configure())) {
            this.runner = r;
            onStart();
            r.run(this::render);
        } finally {
            onStop();
            this.runner = null;
        }
    }

    /**
     * Returns the current runner, or null if not running.
     */
    protected DslRunner runner() {
        return runner;
    }

    /**
     * Signals the application to quit.
     */
    protected void quit() {
        if (runner != null) {
            runner.quit();
        }
    }
}
