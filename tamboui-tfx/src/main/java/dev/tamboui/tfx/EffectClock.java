/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.tfx;

import java.time.Duration;

/**
 * Turns the time of each frame into the {@link TFxDuration} to advance effects by, without
 * losing time to rounding.
 * <p>
 * {@link TFxDuration} counts whole milliseconds, so {@link TFxDuration#fromJavaDuration(Duration)}
 * drops the part of a frame's time below a millisecond. Converted frame by frame, effects fall
 * behind: with 16 ms ticks they run about 3% slow. An effect clock carries that part over to the
 * next frame instead, so the effects are never more than a millisecond behind the time that
 * passed.
 * <p>
 * <b>Usage:</b>
 * <pre>{@code
 * EffectClock clock = new EffectClock();
 *
 * // Each frame, with the time since the previous one
 * effectManager.processEffects(clock.advance(elapsed), frame.buffer(), frame.area());
 * }</pre>
 * <p>
 * Not thread-safe: advance it from the thread that renders.
 */
public final class EffectClock {

    private Duration carried = Duration.ZERO;

    /**
     * Creates a clock with no time carried over.
     */
    public EffectClock() {
    }

    /**
     * Advances this clock by the time of a frame.
     *
     * @param elapsed the time since the previous frame
     * @return the whole milliseconds of {@code elapsed} plus the time carried over from earlier
     *         frames; the part below a millisecond is carried over to the next call
     * @throws IllegalArgumentException if {@code elapsed} is negative
     */
    public TFxDuration advance(Duration elapsed) {
        if (elapsed.isNegative()) {
            throw new IllegalArgumentException("Elapsed time cannot be negative: " + elapsed);
        }
        Duration total = carried.plus(elapsed);
        TFxDuration delta = TFxDuration.fromJavaDuration(total);
        carried = total.minus(delta.toJavaDuration());
        return delta;
    }
}
