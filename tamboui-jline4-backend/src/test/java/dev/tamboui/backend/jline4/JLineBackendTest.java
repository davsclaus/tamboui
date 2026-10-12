/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.backend.jline4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.tamboui.tui.event.Event;
import dev.tamboui.tui.event.EventParser;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.MouseButton;
import dev.tamboui.tui.event.MouseEvent;
import dev.tamboui.tui.event.MouseEventKind;
import dev.tamboui.tui.event.PasteEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Runs the backend on a JLine terminal over in-memory streams, so the tests pin what TamboUI
 * relies on from JLine: size and resize signals, raw mode, decoding of typed input, end of
 * input, and the escape sequences written for the alternate screen and the mouse.
 */
class JLineBackendTest {

    private static final int READ_TIMEOUT_MS = 2000;

    private PipedOutputStream keyboard;
    private ByteArrayOutputStream screen;
    private Terminal terminal;
    private JLineBackend backend;

    @BeforeEach
    void openTerminal() throws IOException {
        keyboard = new PipedOutputStream();
        PipedInputStream input = new PipedInputStream(keyboard, 4096);
        screen = new ByteArrayOutputStream();
        // The exec provider builds a terminal that emulates the line discipline in Java, on
        // these streams only; the native providers would open a real pty for them instead.
        terminal = TerminalBuilder.builder()
                .system(false)
                .provider("exec")
                .streams(input, screen)
                .type("xterm-256color")
                .size(Size.of(80, 24))
                .encoding(StandardCharsets.UTF_8)
                .build();
        backend = new JLineBackend(terminal);
    }

    @AfterEach
    void closeTerminal() throws IOException {
        backend.close();
        keyboard.close();
        terminal.close();
    }

    @Test
    @DisplayName("size() reports the columns and rows of the terminal")
    void reportsTerminalSize() throws IOException {
        assertThat(backend.size().width()).isEqualTo(80);
        assertThat(backend.size().height()).isEqualTo(24);
    }

    @Test
    @DisplayName("a window size change runs the resize handler and size() reports the new size")
    void resizeSignalRunsHandler() throws Exception {
        CountDownLatch resized = new CountDownLatch(1);
        backend.onResize(resized::countDown);

        terminal.setSize(Size.of(100, 30));
        terminal.raise(Terminal.Signal.WINCH);

        assertThat(resized.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(backend.size().width()).isEqualTo(100);
        assertThat(backend.size().height()).isEqualTo(30);
    }

    @Test
    @DisplayName("raw mode turns off signals, echo and line editing, and disabling it restores them")
    void rawModeAndRestore() throws IOException {
        backend.enableRawMode();

        Attributes raw = terminal.getAttributes();
        assertThat(raw.getLocalFlag(Attributes.LocalFlag.ISIG)).isFalse();
        assertThat(raw.getLocalFlag(Attributes.LocalFlag.ICANON)).isFalse();
        assertThat(raw.getLocalFlag(Attributes.LocalFlag.ECHO)).isFalse();

        backend.disableRawMode();

        Attributes restored = terminal.getAttributes();
        assertThat(restored.getLocalFlag(Attributes.LocalFlag.ISIG)).isTrue();
        assertThat(restored.getLocalFlag(Attributes.LocalFlag.ICANON)).isTrue();
        assertThat(restored.getLocalFlag(Attributes.LocalFlag.ECHO)).isTrue();
    }

    @Test
    @DisplayName("typed UTF-8 text is read as code points, Ctrl+C included")
    void readsCodePoints() throws IOException {
        backend.enableRawMode();

        type("a\u00e9\uD83D\uDE00\u0003");

        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo('a');
        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo(0xE9);
        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo(0x1F600);
        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo(3);
    }

    @Test
    @DisplayName("a ZWJ emoji sequence arrives as its code points, in order")
    void readsZwjSequence() throws IOException {
        backend.enableRawMode();

        type("\uD83D\uDC69\u200D\uD83D\uDCBB");

        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo(0x1F469);
        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo(0x200D);
        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo(0x1F4BB);
    }

    @Test
    @DisplayName("peek returns the next code point without consuming it")
    void peekDoesNotConsume() throws IOException {
        backend.enableRawMode();

        type("\uD83D\uDE00x");

        assertThat(backend.peek(READ_TIMEOUT_MS)).isEqualTo(0x1F600);
        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo(0x1F600);
        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo('x');
    }

    @Test
    @DisplayName("read returns -2 when nothing is typed before the timeout")
    void readTimesOut() throws IOException {
        backend.enableRawMode();

        assertThat(backend.read(50)).isEqualTo(-2);
    }

    @Test
    @DisplayName("read returns -1 once the terminal input has ended, so the app can stop")
    void readReportsEndOfInput() throws IOException {
        backend.enableRawMode();

        type("q");
        keyboard.close();

        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo('q');
        assertThat(backend.read(READ_TIMEOUT_MS)).isEqualTo(-1);
    }

    @Test
    @DisplayName("keys, a mouse click and a bracketed paste arrive as TamboUI events")
    void inputArrivesAsEvents() throws IOException {
        backend.enableRawMode();

        type("\u001b[A\r\u001b[<0;10;5M\u001b[200~hello\nworld\u001b[201~");

        Event up = EventParser.readEvent(backend, READ_TIMEOUT_MS);
        assertThat(up).isInstanceOf(KeyEvent.class);
        assertThat(((KeyEvent) up).code()).isEqualTo(KeyCode.UP);

        Event enter = EventParser.readEvent(backend, READ_TIMEOUT_MS);
        assertThat(enter).isInstanceOf(KeyEvent.class);
        assertThat(((KeyEvent) enter).code()).isEqualTo(KeyCode.ENTER);

        Event click = EventParser.readEvent(backend, READ_TIMEOUT_MS);
        assertThat(click).isInstanceOf(MouseEvent.class);
        MouseEvent mouse = (MouseEvent) click;
        assertThat(mouse.kind()).isEqualTo(MouseEventKind.PRESS);
        assertThat(mouse.button()).isEqualTo(MouseButton.LEFT);
        assertThat(mouse.x()).isEqualTo(9);
        assertThat(mouse.y()).isEqualTo(4);

        Event paste = EventParser.readEvent(backend, READ_TIMEOUT_MS);
        assertThat(paste).isInstanceOf(PasteEvent.class);
        assertThat(((PasteEvent) paste).text()).isEqualTo("hello\nworld");
    }

    @Test
    @DisplayName("the alternate screen and mouse capture are switched on, and off again on close")
    void writesAndResetsTerminalModes() throws IOException {
        backend.enterAlternateScreen();
        backend.enableMouseCapture();
        backend.flush();

        assertThat(output()).contains("\u001b[?1049h", "\u001b[?1000h", "\u001b[?1006h");

        screen.reset();
        backend.close();

        assertThat(output()).contains("\u001b[?1049l", "\u001b[?1000l", "\u001b[?1006l", "\u001b[?25h");
    }

    @Test
    @DisplayName("closing the backend leaves a terminal passed in by the caller open")
    void closeKeepsCallerTerminalOpen() throws IOException {
        backend.close();

        assertThatCode(() -> {
            terminal.writer().print("still open");
            terminal.writer().flush();
        }).doesNotThrowAnyException();
        assertThat(output()).endsWith("still open");
    }

    private void type(String text) throws IOException {
        keyboard.write(text.getBytes(StandardCharsets.UTF_8));
        keyboard.flush();
    }

    private String output() {
        return new String(screen.toByteArray(), StandardCharsets.UTF_8);
    }
}
