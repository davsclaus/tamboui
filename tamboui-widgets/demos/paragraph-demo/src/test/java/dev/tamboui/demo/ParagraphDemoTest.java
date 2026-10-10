/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.demo;

import org.junit.jupiter.api.Test;

import dev.tamboui.style.Overflow;

import static org.assertj.core.api.Assertions.assertThat;

class ParagraphDemoTest {

    @Test
    void togglesWrappingWithW() {
        ParagraphDemo demo = new ParagraphDemo();

        assertThat(demo.wrapMode()).isEqualTo(Overflow.WRAP_CHARACTER);
        demo.handleInput('w');
        assertThat(demo.wrapMode()).isEqualTo(Overflow.WRAP_WORD);
        demo.handleInput('W');
        assertThat(demo.wrapMode()).isEqualTo(Overflow.WRAP_CHARACTER);
    }

    @Test
    void scrollingDoesNotChangeWrapping() {
        ParagraphDemo demo = new ParagraphDemo();

        demo.handleInput('w');
        demo.handleInput('j');
        demo.handleInput('k');
        assertThat(demo.wrapMode()).isEqualTo(Overflow.WRAP_WORD);
    }
}
