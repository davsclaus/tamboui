/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.backend.panama.unix;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Decodes the bytes read from the terminal into Unicode code points.
 * <p>
 * Input is decoded as UTF-8 whatever the locale, as the backend writes UTF-8 output.
 * A byte that does not form UTF-8 with the bytes after it, such as an accented letter
 * typed in a terminal set to Latin-1, is returned as it is, and the bytes after it are
 * read again: they are the next keys.
 */
final class Utf8Decoder {

    /**
     * Reads single bytes from the terminal.
     */
    @FunctionalInterface
    interface ByteReader {

        /**
         * Reads one byte.
         *
         * @param timeoutMs timeout in milliseconds (-1 for infinite, 0 for non-blocking)
         * @return the byte, from 0 to 255, -1 for EOF, or -2 for timeout
         * @throws IOException if reading fails
         */
        int read(int timeoutMs) throws IOException;
    }

    // The bytes of one character arrive together, so the rest of a sequence is not waited for long
    private static final int CONTINUATION_TIMEOUT_MS = 50;

    private final ByteReader reader;
    private final Deque<Integer> unread = new ArrayDeque<>();
    private final int[] sequence = new int[4];

    /**
     * Creates a decoder of the bytes from the given reader.
     *
     * @param reader the reader of the terminal bytes
     */
    Utf8Decoder(ByteReader reader) {
        this.reader = reader;
    }

    /**
     * Reads the next byte and, if it is the start of a multi-byte UTF-8 sequence,
     * reads the continuation bytes and returns the decoded Unicode code point.
     *
     * @param timeoutMs timeout in milliseconds (-1 for infinite, 0 for non-blocking)
     * @return the code point, -1 for EOF, or -2 for timeout
     * @throws IOException if reading fails
     */
    int read(int timeoutMs) throws IOException {
        int first = nextByte(timeoutMs);
        if (first < 0x80) {
            // ASCII, EOF (-1), or timeout (-2) — return as-is
            return first;
        }

        // Determine sequence length from the leading byte
        int seqLen;
        if (first < 0xC0) {
            // Lone continuation byte — not valid UTF-8; return as-is
            return first;
        } else if (first < 0xE0) {
            seqLen = 2;
        } else if (first < 0xF0) {
            seqLen = 3;
        } else if (first < 0xF8) {
            seqLen = 4;
        } else {
            return first;
        }

        // Read continuation bytes (they must arrive quickly after the leader)
        int codePoint = first & (0x3F >> (seqLen - 2));
        for (int i = 1; i < seqLen; i++) {
            int b = nextByte(CONTINUATION_TIMEOUT_MS);
            if (b < 0 || (b & 0xC0) != 0x80) {
                // Incomplete or invalid sequence — return the leader byte, and read the
                // bytes after it again
                if (b >= 0) {
                    unread.addFirst(b);
                }
                for (int j = i - 1; j > 0; j--) {
                    unread.addFirst(sequence[j]);
                }
                return first;
            }
            sequence[i] = b;
            codePoint = (codePoint << 6) | (b & 0x3F);
        }
        return codePoint;
    }

    private int nextByte(int timeoutMs) throws IOException {
        Integer b = unread.pollFirst();
        return b != null ? b : reader.read(timeoutMs);
    }
}
