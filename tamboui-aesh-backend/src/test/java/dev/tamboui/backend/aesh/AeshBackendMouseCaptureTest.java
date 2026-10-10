/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.backend.aesh;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AeshBackendMouseCaptureTest {

    private static final String ANY_EVENT_ON = "[?1003h";
    private static final String ANY_EVENT_OFF = "[?1003l";

    @Test
    @DisplayName("motion capture requests any-event tracking so hover (MOVE) events arrive")
    void motionCaptureEnablesAnyEventTracking() throws IOException {
        FakeConnection connection = new FakeConnection();
        AeshBackend backend = new AeshBackend(connection);

        backend.enableMouseCapture(true);

        assertThat(connection.output())
            .contains("[?1000h", "[?1002h", ANY_EVENT_ON, "[?1006h");
    }

    @Test
    @DisplayName("plain capture does not request any-event tracking")
    void plainCaptureDoesNotEnableAnyEventTracking() throws IOException {
        // Motion reports one event per cell crossed, so it must stay opt-in.
        FakeConnection connection = new FakeConnection();
        AeshBackend backend = new AeshBackend(connection);

        backend.enableMouseCapture(false);
        backend.enableMouseCapture();

        assertThat(connection.output())
            .contains("[?1000h", "[?1002h")
            .doesNotContain(ANY_EVENT_ON);
    }

    @Test
    @DisplayName("disabling capture clears any-event tracking")
    void disableClearsAnyEventTracking() throws IOException {
        // Without the reset the terminal keeps streaming motion to the shell after exit.
        FakeConnection connection = new FakeConnection();
        AeshBackend backend = new AeshBackend(connection);

        backend.enableMouseCapture(true);
        backend.disableMouseCapture();

        String output = connection.output();
        assertThat(output.indexOf(ANY_EVENT_OFF)).isGreaterThan(output.indexOf(ANY_EVENT_ON));
    }
}
