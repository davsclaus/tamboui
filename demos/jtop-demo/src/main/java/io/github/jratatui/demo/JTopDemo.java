/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.demo;

import io.github.jratatui.dsl.app.DslRunner;
import io.github.jratatui.style.Color;
import io.github.jratatui.tui.TuiConfig;

import java.time.Duration;

import static io.github.jratatui.dsl.Dsl.*;

/**
 * JTop - A "top" alternative built with JRatatui DSL.
 * <p>
 * Displays system metrics including:
 * <ul>
 *   <li>CPU usage with history sparkline</li>
 *   <li>Memory usage with gauge</li>
 *   <li>Top processes by CPU/memory</li>
 *   <li>System information</li>
 * </ul>
 */
public class JTopDemo {

    public static void main(String[] args) throws Exception {
        new JTopDemo().run();
    }

    public void run() throws Exception {
        var config = TuiConfig.builder()
            .tickRate(Duration.ofMillis(500))
            .build();

        // Create stateful component outside the render supplier
        var systemMonitor = new SystemMonitor();

        try (var runner = DslRunner.create(config)) {
            runner.run(() -> column(
                panel(() -> row(
                    text(" JTop - System Monitor ").bold().cyan(),
                    spacer(),
                    text(" [s] Sort ").dim(),
                    text(" [q] Quit ").dim()
                )).rounded().borderColor(Color.DARK_GRAY).length(3),
                systemMonitor
            ));
        }
    }
}
