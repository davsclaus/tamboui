/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.backend.panama.unix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Utf8DecoderTest {

    @Test
    @DisplayName("a key typed right after a byte that is not UTF-8 is not lost")
    void keyAfterNonUtf8ByteIsKept() throws IOException {
        // é in Latin-1 is 0xE9, which starts a 3-byte sequence in UTF-8
        assertThat(decode(new byte[] {(byte) 0xE9, 'a'})).isEqualTo("éa");
    }

    @Test
    @DisplayName("Latin-1 text comes through one byte per character")
    void latin1Text() throws IOException {
        String text = "Größe »café«";

        assertThat(decode(text.getBytes(StandardCharsets.ISO_8859_1))).isEqualTo(text);
    }

    @Test
    @DisplayName("UTF-8 is still decoded after a byte that is not UTF-8, ZWJ sequences included")
    void utf8AfterNonUtf8Byte() throws IOException {
        String text = "👩\u200D💻 é 👨\u200D👩\u200D👧";
        byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
        byte[] input = new byte[utf8.length + 1];
        input[0] = (byte) 0xE9;
        System.arraycopy(utf8, 0, input, 1, utf8.length);

        assertThat(decode(input)).isEqualTo("é" + text);
    }

    // Decodes the bytes as they arrive from the terminal, until there are no more
    private static String decode(byte[] bytes) throws IOException {
        int[] next = {0};
        Utf8Decoder decoder = new Utf8Decoder(timeoutMs -> next[0] < bytes.length ? bytes[next[0]++] & 0xFF : -2);
        StringBuilder decoded = new StringBuilder();
        for (int codePoint = decoder.read(0); codePoint >= 0; codePoint = decoder.read(0)) {
            decoded.appendCodePoint(codePoint);
        }
        return decoded.toString();
    }
}
