/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.tui.event;

import org.jline.utils.NonBlockingReader;

import java.io.IOException;

/**
 * Parses raw terminal input into typed {@link Event} objects.
 * <p>
 * Handles escape sequences for arrow keys, function keys, navigation keys,
 * and mouse events (SGR extended mode).
 */
public final class EventParser {

    private static final int ESC = 27;
    private static final int PEEK_TIMEOUT = 50;

    private EventParser() {}

    /**
     * Reads and parses the next event from the reader.
     *
     * @param reader  the non-blocking reader
     * @param timeout timeout in milliseconds for the initial read
     * @return the parsed event, or null if no event was available
     * @throws IOException if an I/O error occurs
     */
    public static Event readEvent(NonBlockingReader reader, int timeout) throws IOException {
        int c = reader.read(timeout);

        if (c == -2) {
            // Timeout - no input available
            return null;
        }

        if (c == -1) {
            // EOF
            return null;
        }

        return parseInput(c, reader);
    }

    private static Event parseInput(int c, NonBlockingReader reader) throws IOException {
        if (c == ESC) {
            return parseEscapeSequence(reader);
        }

        // Control characters
        if (c < 32) {
            return parseControlChar(c);
        }

        // Regular printable character
        if (c < 127) {
            return KeyEvent.ofChar((char) c);
        }

        // DEL key
        if (c == 127) {
            return KeyEvent.ofKey(KeyCode.BACKSPACE);
        }

        // Extended ASCII / UTF-8 - treat as character
        return KeyEvent.ofChar((char) c);
    }

    private static Event parseControlChar(int c) {
        switch (c) {
            case 3:
                return KeyEvent.ofChar('c', KeyModifiers.CTRL);  // Ctrl+C
            case 9:
                return KeyEvent.ofKey(KeyCode.TAB);               // Tab
            case 10:
            case 13:
                return KeyEvent.ofKey(KeyCode.ENTER);        // Enter (LF or CR)
            case 27:
                return KeyEvent.ofKey(KeyCode.ESCAPE);           // Escape (standalone)
            default:
                if (c >= 1 && c <= 26) {
                    char letter = (char) ('a' + c - 1);
                    return KeyEvent.ofChar(letter, KeyModifiers.CTRL);
                }
                return KeyEvent.ofKey(KeyCode.UNKNOWN);
        }
    }

    private static Event parseEscapeSequence(NonBlockingReader reader) throws IOException {
        int next = reader.peek(PEEK_TIMEOUT);

        if (next == -2 || next == -1) {
            // Standalone ESC key
            return KeyEvent.ofKey(KeyCode.ESCAPE);
        }

        if (next == '[') {
            reader.read(); // consume '['
            return parseCSI(reader);
        }

        if (next == 'O') {
            reader.read(); // consume 'O'
            return parseSS3(reader);
        }

        // Alt+key
        reader.read(); // consume the character
        if (next >= 'a' && next <= 'z') {
            return KeyEvent.ofChar((char) next, KeyModifiers.ALT);
        }
        if (next >= 'A' && next <= 'Z') {
            return KeyEvent.ofChar((char) next, KeyModifiers.of(false, true, true)); // Alt+Shift
        }

        return KeyEvent.ofKey(KeyCode.UNKNOWN);
    }

    private static Event parseCSI(NonBlockingReader reader) throws IOException {
        int c = reader.read(PEEK_TIMEOUT);
        if (c == -2 || c == -1) {
            return KeyEvent.ofKey(KeyCode.UNKNOWN);
        }

        // Check for mouse event (SGR extended mode: ESC [ < ...)
        if (c == '<') {
            return parseMouseSGR(reader);
        }

        // Arrow keys and simple sequences
        switch (c) {
            case 'A':
                return KeyEvent.ofKey(KeyCode.UP);
            case 'B':
                return KeyEvent.ofKey(KeyCode.DOWN);
            case 'C':
                return KeyEvent.ofKey(KeyCode.RIGHT);
            case 'D':
                return KeyEvent.ofKey(KeyCode.LEFT);
            case 'H':
                return KeyEvent.ofKey(KeyCode.HOME);
            case 'F':
                return KeyEvent.ofKey(KeyCode.END);
            default:
                return parseExtendedCSI(c, reader);
        }
    }

    private static Event parseExtendedCSI(int first, NonBlockingReader reader) throws IOException {
        // Parse numeric parameter(s)
        StringBuilder sb = new StringBuilder();
        sb.append((char) first);

        int c;
        while ((c = reader.read(PEEK_TIMEOUT)) != -2 && c != -1) {
            if (c >= '0' && c <= '9' || c == ';') {
                sb.append((char) c);
            } else {
                // End of sequence
                return parseCSIWithParams(sb.toString(), c);
            }
        }

        return KeyEvent.ofKey(KeyCode.UNKNOWN);
    }

    private static Event parseCSIWithParams(String params, int terminator) {
        // Parse sequences like "1~" (Home), "4~" (End), "5~" (PgUp), etc.
        if (terminator == '~') {
            return parseVT(params);
        }

        // Parse sequences with modifiers like "1;5A" (Ctrl+Up)
        if (terminator >= 'A' && terminator <= 'Z') {
            return parseModifiedArrow(params, terminator);
        }

        return KeyEvent.ofKey(KeyCode.UNKNOWN);
    }

    private static Event parseVT(String params) {
        String[] parts = params.split(";");
        int code;
        try {
            code = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return KeyEvent.ofKey(KeyCode.UNKNOWN);
        }

        KeyModifiers mods = parts.length > 1 ? parseModifierCode(parts[1]) : KeyModifiers.NONE;

        switch (code) {
            case 1:
                return KeyEvent.ofKey(KeyCode.HOME, mods);
            case 2:
                return KeyEvent.ofKey(KeyCode.INSERT, mods);
            case 3:
                return KeyEvent.ofKey(KeyCode.DELETE, mods);
            case 4:
                return KeyEvent.ofKey(KeyCode.END, mods);
            case 5:
                return KeyEvent.ofKey(KeyCode.PAGE_UP, mods);
            case 6:
                return KeyEvent.ofKey(KeyCode.PAGE_DOWN, mods);
            case 11:
                return KeyEvent.ofKey(KeyCode.F1, mods);
            case 12:
                return KeyEvent.ofKey(KeyCode.F2, mods);
            case 13:
                return KeyEvent.ofKey(KeyCode.F3, mods);
            case 14:
                return KeyEvent.ofKey(KeyCode.F4, mods);
            case 15:
                return KeyEvent.ofKey(KeyCode.F5, mods);
            case 17:
                return KeyEvent.ofKey(KeyCode.F6, mods);
            case 18:
                return KeyEvent.ofKey(KeyCode.F7, mods);
            case 19:
                return KeyEvent.ofKey(KeyCode.F8, mods);
            case 20:
                return KeyEvent.ofKey(KeyCode.F9, mods);
            case 21:
                return KeyEvent.ofKey(KeyCode.F10, mods);
            case 23:
                return KeyEvent.ofKey(KeyCode.F11, mods);
            case 24:
                return KeyEvent.ofKey(KeyCode.F12, mods);
            default:
                return KeyEvent.ofKey(KeyCode.UNKNOWN);
        }
    }

    private static Event parseModifiedArrow(String params, int terminator) {
        String[] parts = params.split(";");
        KeyModifiers mods = parts.length > 1 ? parseModifierCode(parts[1]) : KeyModifiers.NONE;

        KeyCode code;
        switch (terminator) {
            case 'A':
                code = KeyCode.UP;
                break;
            case 'B':
                code = KeyCode.DOWN;
                break;
            case 'C':
                code = KeyCode.RIGHT;
                break;
            case 'D':
                code = KeyCode.LEFT;
                break;
            case 'H':
                code = KeyCode.HOME;
                break;
            case 'F':
                code = KeyCode.END;
                break;
            default:
                code = KeyCode.UNKNOWN;
                break;
        }

        return KeyEvent.ofKey(code, mods);
    }

    private static KeyModifiers parseModifierCode(String code) {
        int mod;
        try {
            mod = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return KeyModifiers.NONE;
        }

        // Modifier encoding: 1 + (shift ? 1 : 0) + (alt ? 2 : 0) + (ctrl ? 4 : 0)
        mod = mod - 1;
        boolean shift = (mod & 1) != 0;
        boolean alt = (mod & 2) != 0;
        boolean ctrl = (mod & 4) != 0;

        return KeyModifiers.of(ctrl, alt, shift);
    }

    private static Event parseSS3(NonBlockingReader reader) throws IOException {
        int c = reader.read(PEEK_TIMEOUT);
        if (c == -2 || c == -1) {
            return KeyEvent.ofKey(KeyCode.UNKNOWN);
        }

        // SS3 sequences (typically function keys on some terminals)
        switch (c) {
            case 'P':
                return KeyEvent.ofKey(KeyCode.F1);
            case 'Q':
                return KeyEvent.ofKey(KeyCode.F2);
            case 'R':
                return KeyEvent.ofKey(KeyCode.F3);
            case 'S':
                return KeyEvent.ofKey(KeyCode.F4);
            case 'A':
                return KeyEvent.ofKey(KeyCode.UP);
            case 'B':
                return KeyEvent.ofKey(KeyCode.DOWN);
            case 'C':
                return KeyEvent.ofKey(KeyCode.RIGHT);
            case 'D':
                return KeyEvent.ofKey(KeyCode.LEFT);
            case 'H':
                return KeyEvent.ofKey(KeyCode.HOME);
            case 'F':
                return KeyEvent.ofKey(KeyCode.END);
            default:
                return KeyEvent.ofKey(KeyCode.UNKNOWN);
        }
    }

    private static Event parseMouseSGR(NonBlockingReader reader) throws IOException {
        // SGR mouse format: ESC [ < Cb ; Cx ; Cy M/m
        // where Cb is button code, Cx is column, Cy is row
        // M = press/drag, m = release

        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = reader.read(PEEK_TIMEOUT)) != -2 && c != -1) {
            if (c == 'M' || c == 'm') {
                return parseMouseParams(sb.toString(), c == 'm');
            }
            sb.append((char) c);
        }

        return KeyEvent.ofKey(KeyCode.UNKNOWN);
    }

    private static Event parseMouseParams(String params, boolean isRelease) {
        String[] parts = params.split(";");
        if (parts.length < 3) {
            return KeyEvent.ofKey(KeyCode.UNKNOWN);
        }

        int buttonCode;
        int x;
        int y;
        try {
            buttonCode = Integer.parseInt(parts[0]);
            x = Integer.parseInt(parts[1]) - 1; // Convert to 0-indexed
            y = Integer.parseInt(parts[2]) - 1;
        } catch (NumberFormatException e) {
            return KeyEvent.ofKey(KeyCode.UNKNOWN);
        }

        // Parse modifiers from button code
        boolean shift = (buttonCode & 4) != 0;
        boolean alt = (buttonCode & 8) != 0;
        boolean ctrl = (buttonCode & 16) != 0;
        KeyModifiers mods = KeyModifiers.of(ctrl, alt, shift);

        // Clear modifier bits to get actual button
        int button = buttonCode & ~(4 | 8 | 16);

        // Determine event kind and button
        if (button >= 64 && button <= 65) {
            // Scroll wheel
            MouseEventKind kind = (button == 64) ? MouseEventKind.SCROLL_UP : MouseEventKind.SCROLL_DOWN;
            return new MouseEvent(kind, MouseButton.NONE, x, y, mods);
        }

        boolean isDrag = (button & 32) != 0;
        button = button & ~32;

        MouseButton mouseButton;
        switch (button) {
            case 0:
                mouseButton = MouseButton.LEFT;
                break;
            case 1:
                mouseButton = MouseButton.MIDDLE;
                break;
            case 2:
                mouseButton = MouseButton.RIGHT;
                break;
            default:
                mouseButton = MouseButton.NONE;
                break;
        }

        MouseEventKind kind;
        if (isRelease) {
            kind = MouseEventKind.RELEASE;
        } else if (isDrag) {
            kind = MouseEventKind.DRAG;
        } else if (mouseButton == MouseButton.NONE) {
            kind = MouseEventKind.MOVE;
        } else {
            kind = MouseEventKind.PRESS;
        }

        return new MouseEvent(kind, mouseButton, x, y, mods);
    }
}
