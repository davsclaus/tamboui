/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.list;

import io.github.jratatui.buffer.Buffer;
import io.github.jratatui.layout.Rect;
import io.github.jratatui.style.Color;
import io.github.jratatui.style.Style;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ListWidgetTest {

    @Test
    @DisplayName("ListWidget renders items")
    void rendersItems() {
        List<ListItem> items = Arrays.asList(
            ListItem.from("Item 1"),
            ListItem.from("Item 2"),
            ListItem.from("Item 3")
        );
        ListWidget list = ListWidget.builder()
            .items(items)
            .highlightSymbol("") // No highlight symbol for simple rendering
            .build();
        Rect area = new Rect(0, 0, 20, 3);
        Buffer buffer = Buffer.empty(area);
        ListState state = new ListState();

        list.render(area, buffer, state);

        assertThat(buffer.get(0, 0).symbol()).isEqualTo("I");
        assertThat(buffer.get(0, 1).symbol()).isEqualTo("I");
        assertThat(buffer.get(0, 2).symbol()).isEqualTo("I");
    }

    @Test
    @DisplayName("ListWidget with selection")
    void withSelection() {
        List<ListItem> items = Arrays.asList(
            ListItem.from("Item 1"),
            ListItem.from("Item 2")
        );
        Style highlightStyle = Style.EMPTY.fg(Color.YELLOW);
        ListWidget list = ListWidget.builder()
            .items(items)
            .highlightStyle(highlightStyle)
            .build();
        Rect area = new Rect(0, 0, 20, 2);
        Buffer buffer = Buffer.empty(area);
        ListState state = new ListState();
        state.select(1);

        list.render(area, buffer, state);

        // Second item should have highlight style
        assertThat(buffer.get(0, 1).style().fg()).contains(Color.YELLOW);
    }

    @Test
    @DisplayName("ListWidget with highlight symbol")
    void withHighlightSymbol() {
        List<ListItem> items = Arrays.asList(
            ListItem.from("Item 1"),
            ListItem.from("Item 2")
        );
        ListWidget list = ListWidget.builder()
            .items(items)
            .highlightSymbol("> ")
            .build();
        Rect area = new Rect(0, 0, 20, 2);
        Buffer buffer = Buffer.empty(area);
        ListState state = new ListState();
        state.select(0);

        list.render(area, buffer, state);

        assertThat(buffer.get(0, 0).symbol()).isEqualTo(">");
        assertThat(buffer.get(1, 0).symbol()).isEqualTo(" ");
        assertThat(buffer.get(2, 0).symbol()).isEqualTo("I");
    }

    @Test
    @DisplayName("ListState selection navigation")
    void stateNavigation() {
        ListState state = new ListState();

        assertThat(state.selected()).isNull();

        state.select(0);
        assertThat(state.selected()).isEqualTo(0);

        state.selectNext(5);
        assertThat(state.selected()).isEqualTo(1);

        state.selectPrevious();
        assertThat(state.selected()).isEqualTo(0);

        // Should not go below 0
        state.selectPrevious();
        assertThat(state.selected()).isEqualTo(0);
    }

    @Test
    @DisplayName("ListState selectNext wraps or stops at end")
    void selectNextAtEnd() {
        ListState state = new ListState();
        state.select(4);

        state.selectNext(5);
        assertThat(state.selected()).isEqualTo(4); // stays at end
    }
}
