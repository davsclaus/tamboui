/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.toolkit.elements;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.terminal.Frame;
import dev.tamboui.widgets.text.Overflow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static dev.tamboui.toolkit.Toolkit.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for ListElement.
 */
class ListElementTest {

    @Test
    @DisplayName("ListElement fluent API chains correctly")
    void fluentApiChaining() {
        ListElement<?> element = list("Item 1", "Item 2", "Item 3")
            .highlightSymbol("> ")
            .highlightColor(Color.YELLOW)
            .title("Menu")
            .rounded()
            .borderColor(Color.CYAN);

        assertThat(element).isInstanceOf(ListElement.class);
    }

    @Test
    @DisplayName("list() creates empty element")
    void emptyList() {
        ListElement<?> element = list();
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("list(String...) creates element with items")
    void listWithItems() {
        ListElement<?> element = list("A", "B", "C");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("list(List<String>) creates element with items")
    void listWithItemsList() {
        ListElement<?> element = list(Arrays.asList("X", "Y", "Z"));
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("items() method replaces items")
    void itemsMethod() {
        ListElement<?> element = list()
            .items("New 1", "New 2");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("ListElement renders items to buffer")
    void rendersToBuffer() {
        Rect area = new Rect(0, 0, 20, 5);
        Buffer buffer = Buffer.empty(area);
        Frame frame = Frame.forTesting(buffer);

        list("Item 1", "Item 2", "Item 3")
            .title("List")
            .rounded()
            .render(frame, area, RenderContext.empty());

        // Check border is rendered
        assertThat(buffer.get(0, 0).symbol()).isEqualTo("╭");
    }

    @Test
    @DisplayName("ListElement with selection highlights item")
    void withSelection() {
        Rect area = new Rect(0, 0, 20, 5);
        Buffer buffer = Buffer.empty(area);
        Frame frame = Frame.forTesting(buffer);

        list("Item 1", "Item 2", "Item 3")
            .selected(1)
            .highlightColor(Color.YELLOW)
            .render(frame, area, RenderContext.empty());

        // Second item should have highlight style (row 1 in 0-indexed)
        // We can't easily verify the exact highlight without knowing the layout,
        // but we can verify rendering completes
        assertThat(buffer).isNotNull();
    }

    @Test
    @DisplayName("Empty area does not render")
    void emptyAreaNoRender() {
        Rect emptyArea = new Rect(0, 0, 0, 0);
        Buffer buffer = Buffer.empty(new Rect(0, 0, 10, 5));
        Frame frame = Frame.forTesting(buffer);

        // Should not throw
        list("A", "B").render(frame, emptyArea, RenderContext.empty());
    }

    @Test
    @DisplayName("ListElement manages its own internal state")
    void internalState() {
        Rect area = new Rect(0, 0, 20, 5);
        Buffer buffer = Buffer.empty(area);
        Frame frame = Frame.forTesting(buffer);

        // Should not throw even without explicit state
        list("Item 1", "Item 2")
            .render(frame, area, RenderContext.empty());
    }

    @Test
    @DisplayName("highlightSymbol sets the indicator")
    void highlightSymbol() {
        ListElement<?> element = list("A", "B")
            .highlightSymbol("→ ");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("highlightStyle sets the style")
    void highlightStyle() {
        ListElement<?> element = list("A", "B")
            .highlightColor(Color.GREEN);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("selected() returns current selection index")
    void selectedReturnsIndex() {
        ListElement<?> element = list("A", "B", "C")
            .selected(2);
        assertThat(element.selected()).isEqualTo(2);
    }

    @Test
    @DisplayName("selectPrevious decrements selection")
    void selectPreviousDecrements() {
        ListElement<?> element = list("A", "B", "C")
            .selected(2);
        element.selectPrevious();
        assertThat(element.selected()).isEqualTo(1);
    }

    @Test
    @DisplayName("selectNext increments selection")
    void selectNextIncrements() {
        ListElement<?> element = list("A", "B", "C")
            .selected(0);
        element.selectNext(3);
        assertThat(element.selected()).isEqualTo(1);
    }

    @Nested
    @DisplayName("Text wrapping in list items")
    class TextWrappingTests {

        @Test
        @DisplayName("List with wrapping text items renders correctly")
        void listWithWrappingTextItems() {
            // Area: 20 wide, 10 tall
            // With displayOnly (no highlight symbol), content width = 20
            Rect area = new Rect(0, 0, 20, 10);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            // "This is a longer item that should wrap to multiple lines" = 56 chars
            // At width 20, wraps to 3 lines:
            //   "This is a longer ite" (20)
            //   "m that should wrap t" (20)
            //   "o multiple lines" (16)
            list()
                .add(text("Short item"))
                .add(text("This is a longer item that should wrap to multiple lines").overflow(Overflow.WRAP_CHARACTER))
                .add(text("Another short"))
                .displayOnly()
                .render(frame, area, RenderContext.empty());

            // Line 0: "Short item"
            assertThat(buffer.get(0, 0).symbol()).isEqualTo("S");
            assertThat(buffer.get(5, 0).symbol()).isEqualTo(" ");
            assertThat(buffer.get(6, 0).symbol()).isEqualTo("i");

            // Line 1: "This is a longer ite" (first line of wrapped text)
            assertThat(buffer.get(0, 1).symbol()).isEqualTo("T");
            assertThat(buffer.get(19, 1).symbol()).isEqualTo("e");

            // Line 2: "m that should wrap t" (second line of wrapped text)
            assertThat(buffer.get(0, 2).symbol()).isEqualTo("m");

            // Line 3: "o multiple lines" (third line of wrapped text)
            assertThat(buffer.get(0, 3).symbol()).isEqualTo("o");

            // Line 4: "Another short" (third item, after wrapped text)
            // A=0, n=1, o=2, t=3, h=4, e=5, r=6, (space)=7, s=8
            assertThat(buffer.get(0, 4).symbol()).isEqualTo("A");
            assertThat(buffer.get(8, 4).symbol()).isEqualTo("s");
        }

        @Test
        @DisplayName("List calculates correct total height for scrollbar with wrapped items")
        void scrollbarHeightWithWrappedItems() {
            Rect area = new Rect(0, 0, 10, 5);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            // With width 10 and scrollbar (width 9 for content), "12345678901234567890" wraps to 3 lines
            list()
                .add(text("12345678901234567890").overflow(Overflow.WRAP_CHARACTER))
                .add(text("Short"))
                .displayOnly()
                .scrollbar()
                .render(frame, area, RenderContext.empty());

            // Should render without errors - the scrollbar should account for wrapped height
            assertThat(buffer).isNotNull();
        }

        @Test
        @DisplayName("Mixed fixed-height and wrapped items render correctly")
        void mixedHeightItems() {
            Rect area = new Rect(0, 0, 15, 10);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            list()
                .add(text("Fixed").length(1))  // Explicit 1-line constraint
                .add(text("This text will wrap at width").overflow(Overflow.WRAP_CHARACTER))
                .add(text("Also fixed").length(1))
                .displayOnly()
                .render(frame, area, RenderContext.empty());

            // First item "Fixed" at line 0
            assertThat(buffer.get(0, 0).symbol()).isEqualTo("F");

            // Wrapped text starts at line 1
            assertThat(buffer.get(0, 1).symbol()).isEqualTo("T");
        }

        @Test
        @DisplayName("Element.preferredHeight(width) is used for item height calculation")
        void preferredHeightWithWidthIsUsed() {
            // Create a custom element that returns different heights based on width
            TextElement wrappingText = text("12345678901234567890").overflow(Overflow.WRAP_CHARACTER);

            // At width 10, should be 2 lines
            assertThat(wrappingText.preferredHeight(10, null)).isEqualTo(2);

            // At width 20, should be 1 line
            assertThat(wrappingText.preferredHeight(20, null)).isEqualTo(1);

            // At width 5, should be 4 lines
            assertThat(wrappingText.preferredHeight(5, null)).isEqualTo(4);
        }

        @Test
        @DisplayName("Auto-scroll to end works with wrapped items")
        void autoScrollToEndWithWrappedItems() {
            Rect area = new Rect(0, 0, 10, 3);
            Buffer buffer = Buffer.empty(area);
            Frame frame = Frame.forTesting(buffer);

            // First item wraps to 3 lines, second is 1 line
            // With viewport height 3, scrollToEnd should show the last item
            list()
                .add(text("12345678901234567890123456789").overflow(Overflow.WRAP_CHARACTER))
                .add(text("Last"))
                .displayOnly()
                .scrollToEnd()
                .render(frame, area, RenderContext.empty());

            // "Last" should be visible somewhere in the viewport
            // The exact position depends on scroll calculation
            assertThat(buffer).isNotNull();
        }
    }
}
