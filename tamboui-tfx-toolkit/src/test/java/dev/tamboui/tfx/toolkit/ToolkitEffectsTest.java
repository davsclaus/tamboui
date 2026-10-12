/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.tfx.toolkit;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.StyledAreaRegistry;
import dev.tamboui.terminal.Frame;
import dev.tamboui.tfx.Effect;
import dev.tamboui.tfx.EffectTimer;
import dev.tamboui.tfx.Interpolation;
import dev.tamboui.tfx.effects.FadeShader;
import dev.tamboui.toolkit.AbstractElementTest;
import dev.tamboui.toolkit.app.ToolkitPostRenderProcessor;
import dev.tamboui.toolkit.element.ElementRegistry;
import dev.tamboui.toolkit.focus.FocusManager;
import dev.tamboui.tui.EventHandler;
import dev.tamboui.tui.Renderer;
import dev.tamboui.tui.event.MouseEvent;
import dev.tamboui.tui.event.TickEvent;

import static org.assertj.core.api.Assertions.assertThat;

class ToolkitEffectsTest extends AbstractElementTest {

    private final Frame frame = Frame.forTesting(Buffer.empty(new Rect(0, 0, 10, 2)));

    @Test
    @DisplayName("redraws between two ticks do not advance a running effect")
    void redrawsBetweenTicksDoNotAdvanceEffects() {
        EffectTimer timer = EffectTimer.fromMs(1000, Interpolation.Linear);
        ToolkitEffects effects = new ToolkitEffects();
        effects.addGlobalEffect(Effect.of(FadeShader.fadeTo(Color.WHITE, Color.CYAN, timer)));
        EventHandler handler = effects.wrapHandler((event, runner) -> false);
        Renderer renderer = effects.wrapRenderer(f -> {
        }, new ElementRegistry(), StyledAreaRegistry.create(), new FocusManager());

        assertThat(handler.handle(TickEvent.of(1, Duration.ofMillis(16)), null)).isTrue();
        renderer.render(frame);
        // Mouse moves force a redraw while the effect runs
        for (int i = 0; i < 4; i++) {
            assertThat(handler.handle(MouseEvent.move(i, 0), null)).isTrue();
            renderer.render(frame);
        }
        assertThat(timer.remaining().asMillis()).isEqualTo(984);

        assertThat(handler.handle(TickEvent.of(2, Duration.ofMillis(16)), null)).isTrue();
        renderer.render(frame);
        assertThat(timer.remaining().asMillis()).isEqualTo(968);
    }

    @Test
    @DisplayName("ticks that are not whole milliseconds advance a running effect by all of their time")
    void ticksAdvanceEffectsByAllOfTheirTime() {
        EffectTimer timer = EffectTimer.fromMs(1000, Interpolation.Linear);
        ToolkitEffects effects = new ToolkitEffects();
        effects.addGlobalEffect(Effect.of(FadeShader.fadeTo(Color.WHITE, Color.CYAN, timer)));
        EventHandler handler = effects.wrapHandler((event, runner) -> false);
        Renderer renderer = effects.wrapRenderer(f -> {
        }, new ElementRegistry(), StyledAreaRegistry.create(), new FocusManager());

        // 60 ticks of 16.5 ms are 990 ms
        for (int i = 1; i <= 60; i++) {
            handler.handle(TickEvent.of(i, Duration.ofNanos(16_500_000)), null);
            renderer.render(frame);
        }

        assertThat(timer.remaining().asMillis()).isEqualTo(10);
    }

    @Test
    @DisplayName("the post-render processor advances a running effect by all of the time of its frames")
    void postRenderProcessorAdvancesEffectsByAllOfTheTime() {
        EffectTimer timer = EffectTimer.fromMs(1000, Interpolation.Linear);
        ToolkitEffects effects = new ToolkitEffects();
        effects.addGlobalEffect(Effect.of(FadeShader.fadeTo(Color.WHITE, Color.CYAN, timer)));
        ToolkitPostRenderProcessor processor = effects.asPostRenderProcessor();
        ElementRegistry elementRegistry = new ElementRegistry();
        StyledAreaRegistry styledAreaRegistry = StyledAreaRegistry.create();
        FocusManager focusManager = new FocusManager();

        // 60 frames of 16.5 ms are 990 ms
        for (int i = 0; i < 60; i++) {
            processor.process(frame, elementRegistry, styledAreaRegistry, focusManager, Duration.ofNanos(16_500_000));
        }

        assertThat(timer.remaining().asMillis()).isEqualTo(10);
    }
}
