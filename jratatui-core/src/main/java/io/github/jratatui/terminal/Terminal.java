/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.terminal;

import io.github.jratatui.buffer.Buffer;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.layout.Size;
import io.github.jratatui.buffer.CellUpdate;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * The main terminal abstraction. Manages the rendering lifecycle and
 * buffer management for efficient updates.
 *
 * @param <B> the backend type
 */
public final class Terminal<B extends Backend> implements AutoCloseable {

    private final B backend;
    private Buffer currentBuffer;
    private Buffer previousBuffer;
    private boolean hiddenCursor;

    public Terminal(B backend) throws IOException {
        this.backend = backend;
        this.hiddenCursor = false;

        Size size = backend.size();
        Rect area = Rect.of(size.width(), size.height());
        this.currentBuffer = Buffer.empty(area);
        this.previousBuffer = Buffer.empty(area);
    }

    /**
     * Returns the backend.
     */
    public B backend() {
        return backend;
    }

    /**
     * Returns the current terminal size.
     */
    public Size size() throws IOException {
        return backend.size();
    }

    /**
     * Draws a frame using the provided rendering function.
     * This is the main rendering entry point.
     */
    public CompletedFrame draw(Consumer<Frame> renderer) throws IOException {
        // Handle resize if needed
        Size size = backend.size();
        Rect area = Rect.of(size.width(), size.height());

        if (!area.equals(currentBuffer.area())) {
            resize(area);
        }

        // Clear current buffer for new frame
        currentBuffer.clear();

        // Create frame and render
        Frame frame = new Frame(currentBuffer);
        renderer.accept(frame);

        // Calculate diff and draw
        List<CellUpdate> updates = previousBuffer.diff(currentBuffer);
        if (!updates.isEmpty()) {
            backend.draw(updates);
        }

        // Handle cursor
        if (frame.isCursorVisible()) {
            frame.cursorPosition().ifPresent(pos -> {
                try {
                    backend.setCursorPosition(pos);
                    if (hiddenCursor) {
                        backend.showCursor();
                        hiddenCursor = false;
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to set cursor position", e);
                }
            });
        } else if (!hiddenCursor) {
            backend.hideCursor();
            hiddenCursor = true;
        }

        // Flush output
        backend.flush();

        // Swap buffers
        Buffer temp = previousBuffer;
        previousBuffer = currentBuffer;
        currentBuffer = temp;

        return new CompletedFrame(previousBuffer, area);
    }

    /**
     * Resizes the terminal buffers.
     */
    private void resize(Rect area) throws IOException {
        currentBuffer = Buffer.empty(area);
        previousBuffer = Buffer.empty(area);
        backend.clear();
    }

    /**
     * Clears the terminal and resets the buffers.
     */
    public void clear() throws IOException {
        backend.clear();
        Rect area = currentBuffer.area();
        currentBuffer = Buffer.empty(area);
        previousBuffer = Buffer.empty(area);
    }

    /**
     * Shows the cursor.
     */
    public void showCursor() throws IOException {
        backend.showCursor();
        hiddenCursor = false;
    }

    /**
     * Hides the cursor.
     */
    public void hideCursor() throws IOException {
        backend.hideCursor();
        hiddenCursor = true;
    }

    /**
     * Returns the current terminal area.
     */
    public Rect area() {
        return currentBuffer.area();
    }

    @Override
    public void close() throws IOException {
        if (hiddenCursor) {
            backend.showCursor();
        }
        backend.close();
    }
}
