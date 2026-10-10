/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.backend.aesh;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.tamboui.tui.event.Event;
import dev.tamboui.tui.event.EventParser;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.PasteEvent;

import static org.assertj.core.api.Assertions.assertThat;

class AeshBackendInputTest {

    private final ScheduledExecutorService later = Executors.newSingleThreadScheduledExecutor();

    @AfterEach
    void shutdown() {
        later.shutdownNow();
    }

    @Test
    @DisplayName("peek waits up to its timeout for input that has not arrived yet")
    void peekWaitsForInput() throws Exception {
        FakeConnection connection = new FakeConnection();
        AeshBackend backend = new AeshBackend(connection);
        typeLater(connection, "x", 20);

        assertThat(backend.peek(2000)).isEqualTo('x');
        assertThat(backend.read(0)).isEqualTo('x');
        assertThat(backend.read(0)).isEqualTo(-2);
    }

    @Test
    @DisplayName("an arrow key split across two input chunks is read as one key")
    void arrowKeySplitAcrossChunks() throws Exception {
        FakeConnection connection = new FakeConnection();
        AeshBackend backend = new AeshBackend(connection);
        connection.type("\u001b");
        typeLater(connection, "[A", 10);

        Event event = EventParser.readEvent(backend, 100);

        assertThat(event).isInstanceOf(KeyEvent.class);
        assertThat(((KeyEvent) event).code()).isEqualTo(KeyCode.UP);
    }

    @Test
    @DisplayName("a paste whose end marker is split across two input chunks ends at the marker")
    void pasteEndMarkerSplitAcrossChunks() throws Exception {
        FakeConnection connection = new FakeConnection();
        AeshBackend backend = new AeshBackend(connection);
        connection.type("\u001b[200~hello\u001b");
        typeLater(connection, "[201~", 10);

        Event event = EventParser.readEvent(backend, 100);

        assertThat(event).isInstanceOf(PasteEvent.class);
        assertThat(((PasteEvent) event).text()).isEqualTo("hello");
    }

    private void typeLater(FakeConnection connection, String text, long delayMs) {
        later.schedule(() -> connection.type(text), delayMs, TimeUnit.MILLISECONDS);
    }
}
