/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.backend.aesh;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.aesh.terminal.Attributes;
import org.aesh.terminal.Connection;
import org.aesh.terminal.Device;
import org.aesh.terminal.tty.Capability;
import org.aesh.terminal.tty.Signal;
import org.aesh.terminal.tty.Size;

/** A {@link Connection} for backend tests: records the output and lets a test type input. */
final class FakeConnection implements Connection {
    private final StringBuilder output = new StringBuilder();
    private final Consumer<int[]> stdout = codePoints -> {
        for (int codePoint : codePoints) {
            output.appendCodePoint(codePoint);
        }
    };
    private Consumer<int[]> stdinHandler;
    private Consumer<Signal> signalHandler;
    private Consumer<Size> sizeHandler;
    private Consumer<Void> closeHandler;
    private Attributes attributes = new Attributes();

    @Override
    public Device device() {
        return null;
    }

    @Override
    public Size size() {
        return new Size(80, 24);
    }

    @Override
    public Consumer<int[]> stdinHandler() {
        return stdinHandler;
    }

    @Override
    public void setStdinHandler(Consumer<int[]> handler) {
        this.stdinHandler = handler;
    }

    @Override
    public Consumer<int[]> stdoutHandler() {
        return stdout;
    }

    @Override
    public boolean put(Capability capability, Object... params) {
        return false;
    }

    @Override
    public Consumer<Signal> signalHandler() {
        return signalHandler;
    }

    @Override
    public void setSignalHandler(Consumer<Signal> handler) {
        this.signalHandler = handler;
    }

    @Override
    public Consumer<Size> sizeHandler() {
        return sizeHandler;
    }

    @Override
    public void setSizeHandler(Consumer<Size> handler) {
        this.sizeHandler = handler;
    }

    @Override
    public Consumer<Void> closeHandler() {
        return closeHandler;
    }

    @Override
    public void setCloseHandler(Consumer<Void> handler) {
        this.closeHandler = handler;
    }

    @Override
    public void openBlocking() {
    }

    @Override
    public void openNonBlocking() {
    }

    @Override
    public void close() {
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean supportsAnsi() {
        return true;
    }

    @Override
    public Charset inputEncoding() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public Charset outputEncoding() {
        return StandardCharsets.UTF_8;
    }

    String output() {
        return output.toString();
    }

    /** Delivers {@code text} to the stdin handler as one chunk, as a terminal connection does. */
    void type(String text) {
        stdinHandler.accept(text.codePoints().toArray());
    }
}
