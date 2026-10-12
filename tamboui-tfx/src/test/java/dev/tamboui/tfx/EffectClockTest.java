/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.tfx;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EffectClockTest {

    @Test
    @DisplayName("the part of a frame below a millisecond is carried over to the next frame")
    void carriesThePartBelowAMillisecondOver() {
        EffectClock clock = new EffectClock();
        Duration frame = Duration.ofNanos(16_600_000);

        assertThat(clock.advance(frame)).isEqualTo(TFxDuration.fromMillis(16));
        assertThat(clock.advance(frame)).isEqualTo(TFxDuration.fromMillis(17));
        assertThat(clock.advance(frame)).isEqualTo(TFxDuration.fromMillis(16));
    }

    @Test
    @DisplayName("over many frames the effect time is the time that passed")
    void effectTimeIsTheTimeThatPassed() {
        EffectClock clock = new EffectClock();

        long effectMillis = 0;
        for (int i = 0; i < 1000; i++) {
            effectMillis += clock.advance(Duration.ofNanos(16_500_000)).asMillis();
        }

        assertThat(effectMillis).isEqualTo(16_500);
    }

    @Test
    @DisplayName("frames shorter than a millisecond add up")
    void framesShorterThanAMillisecondAddUp() {
        EffectClock clock = new EffectClock();
        Duration frame = Duration.ofNanos(400_000);

        assertThat(clock.advance(frame).isZero()).isTrue();
        assertThat(clock.advance(frame).isZero()).isTrue();
        assertThat(clock.advance(frame)).isEqualTo(TFxDuration.fromMillis(1));
    }

    @Test
    @DisplayName("a frame without time keeps what is carried over")
    void frameWithoutTimeKeepsTheCarriedTime() {
        EffectClock clock = new EffectClock();

        assertThat(clock.advance(Duration.ofNanos(600_000)).isZero()).isTrue();
        assertThat(clock.advance(Duration.ZERO).isZero()).isTrue();
        assertThat(clock.advance(Duration.ofNanos(400_000))).isEqualTo(TFxDuration.fromMillis(1));
    }

    @Test
    @DisplayName("a negative frame time is rejected")
    void negativeFrameTimeIsRejected() {
        EffectClock clock = new EffectClock();

        assertThatThrownBy(() -> clock.advance(Duration.ofMillis(-1)))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
