/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.css.cascade;

import dev.tamboui.layout.Alignment;
import dev.tamboui.style.Color;
import dev.tamboui.style.Modifier;
import dev.tamboui.style.Style;
import dev.tamboui.style.Width;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Padding;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CssStyleResolverTest {

    @Test
    void withFallbackReturnsThisWhenFallbackIsNull() {
        CssStyleResolver resolver = CssStyleResolver.builder()
                .foreground(Color.RED)
                .build();

        CssStyleResolver result = resolver.withFallback(null);

        assertThat(result).isSameAs(resolver);
    }

    @Test
    void withFallbackUsesParentPropertiesWhenChildDoesNotHaveThem() {
        CssStyleResolver parent = CssStyleResolver.builder()
                .foreground(Color.BLUE)
                .background(Color.WHITE)
                .borderType(BorderType.DOUBLE)
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .foreground(Color.RED)
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        // Child's foreground should override parent's
        assertThat(merged.foreground()).contains(Color.RED);
        // Parent's background should be inherited
        assertThat(merged.background()).contains(Color.WHITE);
        // Parent's borderType should be inherited
        assertThat(merged.borderType()).contains(BorderType.DOUBLE);
    }

    @Test
    void withFallbackChildPropertiesTakePrecedence() {
        CssStyleResolver parent = CssStyleResolver.builder()
                .foreground(Color.BLUE)
                .background(Color.WHITE)
                .alignment(Alignment.CENTER)
                .borderType(BorderType.PLAIN)
                .width(Width.FILL)
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .foreground(Color.RED)
                .background(Color.BLACK)
                .alignment(Alignment.LEFT)
                .borderType(BorderType.DOUBLE)
                .width(Width.FIT)
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        // All properties should come from child since it has them all
        assertThat(merged.foreground()).contains(Color.RED);
        assertThat(merged.background()).contains(Color.BLACK);
        assertThat(merged.alignment()).contains(Alignment.LEFT);
        assertThat(merged.borderType()).contains(BorderType.DOUBLE);
        assertThat(merged.width()).isEqualTo(Width.FIT);
    }

    @Test
    void withFallbackMergesModifiersPreferringChild() {
        CssStyleResolver parent = CssStyleResolver.builder()
                .addModifier(Modifier.BOLD)
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .addModifier(Modifier.ITALIC)
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        // Child has modifiers, so child's modifiers take precedence
        assertThat(merged.modifiers()).containsExactly(Modifier.ITALIC);
    }

    @Test
    void withFallbackInheritsModifiersWhenChildHasNone() {
        CssStyleResolver parent = CssStyleResolver.builder()
                .addModifier(Modifier.BOLD)
                .addModifier(Modifier.UNDERLINED)
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .foreground(Color.RED)
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        // Child has no modifiers, so parent's modifiers should be inherited
        assertThat(merged.modifiers()).contains(Modifier.BOLD, Modifier.UNDERLINED);
    }

    @Test
    void withFallbackHandlesEmptyModifiersOnBothSides() {
        CssStyleResolver parent = CssStyleResolver.builder()
                .foreground(Color.BLUE)
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .foreground(Color.RED)
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        // Both have empty modifiers - should not cause EnumSet.copyOf issue
        assertThat(merged.modifiers()).isEmpty();
    }

    @Test
    void withFallbackMergesAdditionalProperties() {
        CssStyleResolver parent = CssStyleResolver.builder()
                .property("border-color", "blue")
                .property("parent-only", "value1")
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .property("border-color", "red")
                .property("child-only", "value2")
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        // Child's border-color should override parent's
        assertThat(merged.getProperty("border-color")).contains("red");
        // Parent-only property should be inherited
        assertThat(merged.getProperty("parent-only")).contains("value1");
        // Child-only property should be present
        assertThat(merged.getProperty("child-only")).contains("value2");
    }

    @Test
    void withFallbackHandlesEmptyAdditionalPropertiesOnChild() {
        CssStyleResolver parent = CssStyleResolver.builder()
                .property("key1", "value1")
                .property("key2", "value2")
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .foreground(Color.RED)
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        assertThat(merged.getProperty("key1")).contains("value1");
        assertThat(merged.getProperty("key2")).contains("value2");
    }

    @Test
    void withFallbackHandlesEmptyAdditionalPropertiesOnParent() {
        CssStyleResolver parent = CssStyleResolver.builder()
                .foreground(Color.BLUE)
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .property("key1", "value1")
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        assertThat(merged.getProperty("key1")).contains("value1");
    }

    @Test
    void withFallbackInheritsPadding() {
        Padding parentPadding = Padding.uniform(2);
        CssStyleResolver parent = CssStyleResolver.builder()
                .padding(parentPadding)
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .foreground(Color.RED)
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        assertThat(merged.padding()).contains(parentPadding);
    }

    @Test
    void toStyleIncludesInheritedProperties() {
        CssStyleResolver parent = CssStyleResolver.builder()
                .background(Color.WHITE)
                .addModifier(Modifier.BOLD)
                .build();

        CssStyleResolver child = CssStyleResolver.builder()
                .foreground(Color.RED)
                .build();

        CssStyleResolver merged = child.withFallback(parent);

        Style style = merged.toStyle();
        assertThat(style.fg()).contains(Color.RED);
        assertThat(style.bg()).contains(Color.WHITE);
        assertThat(style.addModifiers()).contains(Modifier.BOLD);
    }
}