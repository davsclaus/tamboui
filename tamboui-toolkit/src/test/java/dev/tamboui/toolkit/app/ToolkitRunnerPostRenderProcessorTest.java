/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.toolkit.app;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.tamboui.terminal.TestBackend;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.TuiConfig;
import dev.tamboui.tui.TuiRunner;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.ResizeEvent;
import dev.tamboui.tui.event.TickEvent;

import static dev.tamboui.toolkit.Toolkit.panel;
import static org.assertj.core.api.Assertions.assertThat;

class ToolkitRunnerPostRenderProcessorTest {

    @Test
    @DisplayName("post-render processors get a tick's time once, not again on redraws before the next tick")
    void postRenderProcessorsGetTickTimeOnce() throws Exception {
        BlockingQueue<Duration> elapsedPerFrame = new LinkedBlockingQueue<>();
        TuiConfig config = TuiConfig.builder()
                .rawMode(false)
                .alternateScreen(false)
                .hideCursor(false)
                .shutdownHook(false)
                .pollTimeout(Duration.ofMillis(10))
                .noTick()
                .backend(new TestBackend(40, 10))
                .build();
        ToolkitRunner runner = ToolkitRunner.builder()
                .config(config)
                .postRenderProcessor((frame, elements, styledAreas, focus, elapsed) -> elapsedPerFrame.add(elapsed))
                .build();
        Thread renderThread = new Thread(() -> {
            try {
                runner.run(() -> panel("fx").id("main").focusable().onKeyEvent(event -> EventResult.HANDLED));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }, "toolkit-runner-test");
        renderThread.setDaemon(true);
        renderThread.start();
        try {
            TuiRunner tui = runner.tuiRunner();
            assertThat(nextFrame(elapsedPerFrame)).isEqualTo(Duration.ZERO);

            tui.dispatch(TickEvent.of(1, Duration.ofMillis(16)));
            assertThat(nextFrame(elapsedPerFrame)).isEqualTo(Duration.ofMillis(16));

            // Redraws for two key presses and a resize before the next tick
            tui.dispatch(KeyEvent.ofChar('x'));
            assertThat(nextFrame(elapsedPerFrame)).isEqualTo(Duration.ZERO);
            tui.dispatch(KeyEvent.ofChar('x'));
            assertThat(nextFrame(elapsedPerFrame)).isEqualTo(Duration.ZERO);
            tui.dispatch(ResizeEvent.of(40, 10));
            assertThat(nextFrame(elapsedPerFrame)).isEqualTo(Duration.ZERO);

            tui.dispatch(TickEvent.of(2, Duration.ofMillis(20)));
            assertThat(nextFrame(elapsedPerFrame)).isEqualTo(Duration.ofMillis(20));
        } finally {
            runner.quit();
            renderThread.join(1000);
            runner.close();
        }
    }

    private static Duration nextFrame(BlockingQueue<Duration> elapsedPerFrame) throws InterruptedException {
        return elapsedPerFrame.poll(5, TimeUnit.SECONDS);
    }
}
