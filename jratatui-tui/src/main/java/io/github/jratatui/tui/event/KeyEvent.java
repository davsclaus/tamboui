/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.tui.event;

/**
 * Represents a keyboard input event.
 */
public final class KeyEvent implements Event {

    private final KeyCode code;
    private final KeyModifiers modifiers;
    private final char character;

    public KeyEvent(KeyCode code, KeyModifiers modifiers, char character) {
        this.code = code;
        this.modifiers = modifiers;
        this.character = character;
    }

    /**
     * Creates a key event for a printable character.
     */
    public static KeyEvent ofChar(char c) {
        return new KeyEvent(KeyCode.CHAR, KeyModifiers.NONE, c);
    }

    /**
     * Creates a key event for a printable character with modifiers.
     */
    public static KeyEvent ofChar(char c, KeyModifiers modifiers) {
        return new KeyEvent(KeyCode.CHAR, modifiers, c);
    }

    /**
     * Creates a key event for a special key.
     */
    public static KeyEvent ofKey(KeyCode code) {
        return new KeyEvent(code, KeyModifiers.NONE, '\0');
    }

    /**
     * Creates a key event for a special key with modifiers.
     */
    public static KeyEvent ofKey(KeyCode code, KeyModifiers modifiers) {
        return new KeyEvent(code, modifiers, '\0');
    }

    /**
     * Returns true if this is a character event matching the given character.
     */
    public boolean isChar(char c) {
        return code == KeyCode.CHAR && character == c;
    }

    /**
     * Returns true if this is a key event matching the given key code.
     */
    public boolean isKey(KeyCode keyCode) {
        return code == keyCode;
    }

    /**
     * Returns true if Ctrl modifier was pressed.
     */
    public boolean hasCtrl() {
        return modifiers.ctrl();
    }

    /**
     * Returns true if Alt modifier was pressed.
     */
    public boolean hasAlt() {
        return modifiers.alt();
    }

    /**
     * Returns true if Shift modifier was pressed.
     */
    public boolean hasShift() {
        return modifiers.shift();
    }

    /**
     * Returns true if this is a Ctrl+C event (common quit signal).
     */
    public boolean isCtrlC() {
        return hasCtrl() && isChar('c');
    }

    public KeyCode code() {
        return code;
    }

    public KeyModifiers modifiers() {
        return modifiers;
    }

    public char character() {
        return character;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyEvent)) {
            return false;
        }
        KeyEvent keyEvent = (KeyEvent) o;
        return character == keyEvent.character
            && code == keyEvent.code
            && modifiers.equals(keyEvent.modifiers);
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + Character.hashCode(character);
        return result;
    }

    @Override
    public String toString() {
        return String.format("KeyEvent[code=%s, modifiers=%s, character=%s]", code, modifiers, character);
    }
}
