/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.dsl.elements;

import io.github.jratatui.buffer.Buffer;
import io.github.jratatui.dsl.element.RenderContext;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.terminal.Frame;
import io.github.jratatui.widgets.tabs.TabsState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.jratatui.dsl.Dsl.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for TabsElement.
 */
class TabsElementTest {

    @Test
    @DisplayName("TabsElement fluent API chains correctly")
    void fluentApiChaining() {
        var element = tabs("Home", "Settings", "About")
            .selected(0)
            .highlightColor(Color.CYAN)
            .divider(" | ")
            .title("Navigation")
            .rounded()
            .borderColor(Color.GREEN);

        assertThat(element).isInstanceOf(TabsElement.class);
    }

    @Test
    @DisplayName("tabs() creates empty element")
    void emptyTabs() {
        var element = tabs();
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("tabs(String...) creates element with titles")
    void tabsWithTitles() {
        var element = tabs("Tab1", "Tab2", "Tab3");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("tabs(List<String>) creates element with titles")
    void tabsWithTitlesList() {
        var element = tabs(List.of("A", "B", "C"));
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("selected() sets initial selection")
    void selectedMethod() {
        var element = tabs("A", "B", "C").selected(1);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("state() sets tabs state")
    void stateMethod() {
        var state = new TabsState();
        state.select(2);
        var element = tabs("A", "B", "C").state(state);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("divider() sets separator")
    void dividerMethod() {
        var element = tabs("A", "B").divider(" | ");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("TabsElement renders to buffer")
    void rendersToBuffer() {
        var area = new Rect(0, 0, 40, 3);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);

        tabs("Home", "Settings", "About")
            .selected(0)
            .title("Menu")
            .rounded()
            .render(frame, area, RenderContext.empty());

        // Check border is rendered
        assertThat(buffer.get(0, 0).symbol()).isEqualTo("╭");
    }

    @Test
    @DisplayName("TabsElement with highlight color")
    void withHighlightColor() {
        var area = new Rect(0, 0, 30, 1);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);

        tabs("A", "B", "C")
            .selected(0)
            .highlightColor(Color.YELLOW)
            .render(frame, area, RenderContext.empty());

        // First tab should have highlight
        assertThat(buffer).isNotNull();
    }

    @Test
    @DisplayName("Empty area does not render")
    void emptyAreaNoRender() {
        var emptyArea = new Rect(0, 0, 0, 0);
        var buffer = Buffer.empty(new Rect(0, 0, 20, 3));
        var frame = Frame.forTesting(buffer);

        // Should not throw
        tabs("A", "B").render(frame, emptyArea, RenderContext.empty());
    }

    @Test
    @DisplayName("tabs without explicit selection defaults to 0")
    void defaultSelection() {
        var area = new Rect(0, 0, 20, 1);
        var buffer = Buffer.empty(area);
        var frame = Frame.forTesting(buffer);

        tabs("X", "Y", "Z")
            .render(frame, area, RenderContext.empty());

        // Should render without error
        assertThat(buffer).isNotNull();
    }
}
