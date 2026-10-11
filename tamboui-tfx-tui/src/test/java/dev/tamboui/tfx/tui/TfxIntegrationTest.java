/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.tfx.tui;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.terminal.Frame;
import dev.tamboui.tfx.Effect;
import dev.tamboui.tfx.EffectTimer;
import dev.tamboui.tfx.Interpolation;
import dev.tamboui.tfx.effects.FadeShader;
import dev.tamboui.tui.EventHandler;
import dev.tamboui.tui.Renderer;
import dev.tamboui.tui.event.MouseEvent;
import dev.tamboui.tui.event.TickEvent;

import static org.assertj.core.api.Assertions.assertThat;

class TfxIntegrationTest {

    private final Frame frame = Frame.forTesting(Buffer.empty(new Rect(0, 0, 10, 2)));

    @Test
    @DisplayName("redraws between two ticks do not advance a running effect")
    void redrawsBetweenTicksDoNotAdvanceEffects() {
        EffectTimer timer = EffectTimer.fromMs(1000, Interpolation.Linear);
        TfxIntegration tfx = new TfxIntegration();
        tfx.addEffect(Effect.of(FadeShader.fadeTo(Color.WHITE, Color.CYAN, timer)));
        EventHandler handler = tfx.wrapHandler((event, runner) -> false);
        Renderer renderer = tfx.wrapRenderer(f -> {
        });

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
}
