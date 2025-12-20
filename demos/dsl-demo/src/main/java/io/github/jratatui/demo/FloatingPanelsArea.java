/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.demo;

import io.github.jratatui.dsl.element.Element;
import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.dsl.event.EventResult;
import io.github.jratatui.layout.Constraint;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.tui.Keys;
import io.github.jratatui.tui.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static io.github.jratatui.dsl.Dsl.panel;

/**
 * Custom element that renders and manages floating panels.
 */
final class FloatingPanelsArea implements Element {

    private final AtomicLong tickCount = new AtomicLong(0);
    private final List<FloatingPanel> panels = new ArrayList<>();
    private final Random random = new Random();
    private int nextPanelId = 1;

    FloatingPanelsArea() {
        createInitialPanels();
    }

    private void createInitialPanels() {
        panels.add(new FloatingPanel(nextPanelId++, new ClockPanel(), 50, 1));
        panels.add(new FloatingPanel(nextPanelId++, new SystemInfoPanel(this::formatUptime), 2, 1));
        panels.add(new FloatingPanel(nextPanelId++, new TodoPanel("Learn JRatatui DSL", "Build awesome TUI apps", "Share with the world"), 2, 9));
        panels.add(new FloatingPanel(nextPanelId++, new CounterPanel(), 35, 9));
        panels.add(new FloatingPanel(nextPanelId++, new ProgressPanel(), 50, 9));
    }

    private void createPanel(PanelContent content) {
        var x = 5 + random.nextInt(30);
        var y = 3 + random.nextInt(10);
        panels.add(new FloatingPanel(nextPanelId++, content, x, y));
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        var tick = tickCount.incrementAndGet();
        for (var fp : panels) {
            fp.content.onTick(tick);
        }

        for (var fp : panels) {
            renderFloatingPanel(frame, area, context, fp);
        }
    }

    private void renderFloatingPanel(Frame frame, Rect area, RenderContext context, FloatingPanel fp) {
        var content = fp.content;
        var relX = Math.max(0, Math.min(fp.x, area.width() - content.width()));
        var relY = Math.max(0, Math.min(fp.y, area.height() - content.height()));
        var panelArea = new Rect(area.x() + relX, area.y() + relY, content.width(), content.height());

        var focused = fp.panelId().equals(context.focusManager().focusedId());
        var borderColor = focused ? Color.WHITE : content.color();

        var p = panel(content.title(), () -> content.render(focused))
                .id(fp.panelId())
                .rounded()
                .borderColor(borderColor)
                .focusedBorderColor(Color.WHITE)
                .focusable()
                .onKeyEvent(event -> handlePanelKey(fp, event))
                .draggable((deltaX, deltaY) -> {
                    fp.x += deltaX;
                    fp.y += deltaY;
                });

        p.render(frame, panelArea, context);
    }

    private EventResult handlePanelKey(FloatingPanel fp, KeyEvent event) {
        var result = fp.content.handleKey(event);
        if (result.isHandled()) {
            return result;
        }

        if (Keys.isChar(event, 'x') || Keys.isChar(event, 'X')) {
            panels.removeIf(p -> p.id == fp.id);
            return EventResult.HANDLED;
        }

        return EventResult.UNHANDLED;
    }

    @Override
    public Constraint constraint() {
        return Constraint.fill();
    }

    @Override
    public EventResult handleKeyEvent(KeyEvent event, boolean focused) {
        if (Keys.isChar(event, '1')) { createPanel(new ClockPanel()); return EventResult.HANDLED; }
        if (Keys.isChar(event, '2')) { createPanel(new CounterPanel()); return EventResult.HANDLED; }
        if (Keys.isChar(event, '3')) { createPanel(new SystemInfoPanel(this::formatUptime)); return EventResult.HANDLED; }
        if (Keys.isChar(event, '4')) { createPanel(new QuotePanel()); return EventResult.HANDLED; }
        if (Keys.isChar(event, '5')) { createPanel(new ProgressPanel()); return EventResult.HANDLED; }
        if (Keys.isChar(event, '6')) { createPanel(new TodoPanel()); return EventResult.HANDLED; }
        return EventResult.UNHANDLED;
    }

    private String formatUptime() {
        var ticks = tickCount.get();
        var seconds = ticks / 10;
        var minutes = seconds / 60;
        var hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }
}
