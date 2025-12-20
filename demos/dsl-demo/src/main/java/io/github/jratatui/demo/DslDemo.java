/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.demo;

import io.github.jratatui.dsl.app.DslRunner;
import io.github.jratatui.dsl.element.Element;
import io.github.jratatui.style.Color;
import io.github.jratatui.tui.TuiConfig;

import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static io.github.jratatui.dsl.Dsl.*;

/**
 * Widget Playground Demo showcasing the JRatatui DSL.
 * <p>
 * Features demonstrated:
 * <ul>
 *   <li>Lambda-based DSL for dynamic content</li>
 *   <li>Stateful components (TODO list with input field)</li>
 *   <li>Event handlers on elements</li>
 *   <li>Draggable floating panels</li>
 *   <li>Focus navigation (Tab/Shift+Tab)</li>
 *   <li>Dynamic component creation</li>
 * </ul>
 */
public class DslDemo {

    public DslDemo() {
    }

    public static void main(String[] args) throws Exception {
        new DslDemo().run();
    }

    public void run() throws Exception {
        var config = TuiConfig.builder()
            .mouseCapture(true)
            .tickRate(Duration.ofMillis(100))
            .build();

        try (var runner = DslRunner.create(config)) {
            var panels = new FloatingPanelsArea();
            runner.run(() -> column(
                    panel(() -> row(
                            text(" JRatatui Widget Playground ").bold().cyan(),
                            spacer(),
                            text(" [1-6] Add Panel ").dim(),
                            text(" [Tab] Focus ").dim(),
                            text(" [Drag] Move ").dim(),
                            text(" [x] Delete ").dim(),
                            text(" [q] Quit ").dim()
                    )).rounded().borderColor(Color.DARK_GRAY).length(3),
                    panels
            ));
        }
    }

}
