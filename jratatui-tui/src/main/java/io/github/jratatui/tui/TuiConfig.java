/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.tui;

import java.time.Duration;

/**
 * Configuration options for {@link TuiRunner}.
 */
public final class TuiConfig {

    private final boolean rawMode;
    private final boolean alternateScreen;
    private final boolean hideCursor;
    private final boolean mouseCapture;
    private final Duration pollTimeout;
    private final Duration tickRate;
    private final boolean shutdownHook;

    public TuiConfig(
            boolean rawMode,
            boolean alternateScreen,
            boolean hideCursor,
            boolean mouseCapture,
            Duration pollTimeout,
            Duration tickRate,
            boolean shutdownHook
    ) {
        this.rawMode = rawMode;
        this.alternateScreen = alternateScreen;
        this.hideCursor = hideCursor;
        this.mouseCapture = mouseCapture;
        this.pollTimeout = pollTimeout;
        this.tickRate = tickRate;
        this.shutdownHook = shutdownHook;
    }

    /**
     * Returns the default configuration.
     */
    public static TuiConfig defaults() {
        return new TuiConfig(
                true,                        // rawMode
                true,                        // alternateScreen
                true,                        // hideCursor
                false,                       // mouseCapture
                Duration.ofMillis(100),      // pollTimeout
                null,                         // tickRate (disabled),
                true
        );
    }

    /**
     * Returns a configuration suitable for animated applications.
     *
     * @param tickRate the interval between tick events (e.g., Duration.ofMillis(16) for ~60fps)
     */
    public static TuiConfig withAnimation(Duration tickRate) {
        return builder().tickRate(tickRate).build();
    }

    /**
     * Returns a new builder with default values.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns true if tick events are enabled.
     */
    public boolean ticksEnabled() {
        return tickRate != null;
    }

    public boolean rawMode() {
        return rawMode;
    }

    public boolean alternateScreen() {
        return alternateScreen;
    }

    public boolean hideCursor() {
        return hideCursor;
    }

    public boolean mouseCapture() {
        return mouseCapture;
    }

    public Duration pollTimeout() {
        return pollTimeout;
    }

    public Duration tickRate() {
        return tickRate;
    }

    public boolean shutdownHook() {
        return shutdownHook;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TuiConfig)) {
            return false;
        }
        TuiConfig that = (TuiConfig) o;
        return rawMode == that.rawMode
                && alternateScreen == that.alternateScreen
                && hideCursor == that.hideCursor
                && mouseCapture == that.mouseCapture
                && pollTimeout.equals(that.pollTimeout)
                && (tickRate != null ? tickRate.equals(that.tickRate) : that.tickRate == null);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(rawMode);
        result = 31 * result + Boolean.hashCode(alternateScreen);
        result = 31 * result + Boolean.hashCode(hideCursor);
        result = 31 * result + Boolean.hashCode(mouseCapture);
        result = 31 * result + pollTimeout.hashCode();
        result = 31 * result + (tickRate != null ? tickRate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format(
                "TuiConfig[rawMode=%s, alternateScreen=%s, hideCursor=%s, mouseCapture=%s, pollTimeout=%s, tickRate=%s, shutdownHook=%s]",
                rawMode,
                alternateScreen,
                hideCursor,
                mouseCapture,
                pollTimeout,
                tickRate,
                shutdownHook
        );
    }

    /**
     * Builder for {@link TuiConfig}.
     */
    public static final class Builder {
        private boolean rawMode = true;
        private boolean alternateScreen = true;
        private boolean hideCursor = true;
        private boolean mouseCapture = false;
        private Duration pollTimeout = Duration.ofMillis(100);
        private Duration tickRate = null;
        private boolean shutdownHook = true;

        private Builder() {
        }

        public Builder shutdownHook(boolean shutdownHook) {
            this.shutdownHook = shutdownHook;
            return this;
        }

        /**
         * Sets whether to enable raw mode.
         */
        public Builder rawMode(boolean rawMode) {
            this.rawMode = rawMode;
            return this;
        }

        /**
         * Sets whether to use alternate screen buffer.
         */
        public Builder alternateScreen(boolean alternateScreen) {
            this.alternateScreen = alternateScreen;
            return this;
        }

        /**
         * Sets whether to hide the cursor.
         */
        public Builder hideCursor(boolean hideCursor) {
            this.hideCursor = hideCursor;
            return this;
        }

        /**
         * Sets whether to capture mouse events.
         */
        public Builder mouseCapture(boolean mouseCapture) {
            this.mouseCapture = mouseCapture;
            return this;
        }

        /**
         * Sets the timeout for polling events.
         */
        public Builder pollTimeout(Duration pollTimeout) {
            this.pollTimeout = pollTimeout != null ? pollTimeout : Duration.ofMillis(100);
            return this;
        }

        /**
         * Sets the interval between tick events.
         * Set to null to disable tick events.
         *
         * @param tickRate the tick interval (e.g., Duration.ofMillis(16) for ~60fps)
         */
        public Builder tickRate(Duration tickRate) {
            this.tickRate = tickRate;
            return this;
        }

        /**
         * Builds the configuration.
         */
        public TuiConfig build() {
            return new TuiConfig(
                    rawMode,
                    alternateScreen,
                    hideCursor,
                    mouseCapture,
                    pollTimeout,
                    tickRate,
                    shutdownHook
            );
        }
    }
}
