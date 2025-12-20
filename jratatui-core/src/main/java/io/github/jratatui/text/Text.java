/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.text;

import io.github.jratatui.layout.Alignment;
import io.github.jratatui.style.Color;
import io.github.jratatui.style.Style;
import static io.github.jratatui.util.CollectionUtil.listCopyOf;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Multi-line styled text, composed of Lines.
 */
public final class Text {

    private final List<Line> lines;
    private final Optional<Alignment> alignment;

    public Text(List<Line> lines, Optional<Alignment> alignment) {
        this.lines = listCopyOf(lines);
        this.alignment = alignment != null ? alignment : Optional.empty();
    }

    public static Text empty() {
        return new Text(listCopyOf(), Optional.empty());
    }

    public static Text raw(String text) {
        List<Line> linesList = new BufferedReader(new StringReader(text))
            .lines()
            .map(Line::from)
            .collect(Collectors.toList());
        return new Text(linesList, Optional.empty());
    }

    public static Text from(String text) {
        return raw(text);
    }

    public static Text from(Line line) {
        return new Text(listCopyOf(line), Optional.empty());
    }

    public static Text from(Line... lines) {
        return new Text(Arrays.asList(lines), Optional.empty());
    }

    public static Text from(List<Line> lines) {
        return new Text(lines, Optional.empty());
    }

    public static Text from(Span span) {
        return new Text(listCopyOf(Line.from(span)), Optional.empty());
    }

    public static Text styled(String text, Style style) {
        List<Line> linesList = new BufferedReader(new StringReader(text))
            .lines()
            .map(line -> Line.styled(line, style))
            .collect(Collectors.toList());
        return new Text(linesList, Optional.empty());
    }

    /**
     * Returns the height (number of lines).
     */
    public int height() {
        return lines.size();
    }

    /**
     * Returns the maximum width of any line.
     */
    public int width() {
        return lines.stream()
            .mapToInt(Line::width)
            .max()
            .orElse(0);
    }

    public boolean isEmpty() {
        return lines.isEmpty() || lines.stream().allMatch(Line::isEmpty);
    }

    public Text alignment(Alignment alignment) {
        return new Text(lines, Optional.of(alignment));
    }

    public Text left() {
        return alignment(Alignment.LEFT);
    }

    public Text centered() {
        return alignment(Alignment.CENTER);
    }

    public Text right() {
        return alignment(Alignment.RIGHT);
    }

    /**
     * Applies a style patch to all lines.
     */
    public Text patchStyle(Style style) {
        List<Line> newLines = lines.stream()
            .map(line -> line.patchStyle(style))
            .collect(Collectors.toList());
        return new Text(newLines, alignment);
    }

    public Text fg(Color color) {
        return patchStyle(Style.EMPTY.fg(color));
    }

    public Text bg(Color color) {
        return patchStyle(Style.EMPTY.bg(color));
    }

    public Text bold() {
        return patchStyle(Style.EMPTY.bold());
    }

    public Text italic() {
        return patchStyle(Style.EMPTY.italic());
    }

    public Text underlined() {
        return patchStyle(Style.EMPTY.underlined());
    }

    /**
     * Returns the raw text content without styling.
     */
    public String rawContent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(lines.get(i).rawContent());
        }
        return sb.toString();
    }

    /**
     * Appends another text's lines to this text.
     */
    public Text append(Text other) {
        List<Line> newLines = new ArrayList<>(lines);
        newLines.addAll(other.lines);
        return new Text(newLines, alignment);
    }

    /**
     * Appends a line to this text.
     */
    public Text append(Line line) {
        List<Line> newLines = new ArrayList<>(lines);
        newLines.add(line);
        return new Text(newLines, alignment);
    }

    /**
     * Extends the last line with the given span, or adds a new line if empty.
     */
    public Text push(Span span) {
        if (lines.isEmpty()) {
            return append(Line.from(span));
        }
        List<Line> newLines = new ArrayList<>(lines);
        Line lastLine = newLines.remove(newLines.size() - 1);
        newLines.add(lastLine.append(span));
        return new Text(newLines, alignment);
    }

    public List<Line> lines() {
        return lines;
    }

    public Optional<Alignment> alignment() {
        return alignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Text)) {
            return false;
        }
        Text text = (Text) o;
        return lines.equals(text.lines) && alignment.equals(text.alignment);
    }

    @Override
    public int hashCode() {
        int result = lines.hashCode();
        result = 31 * result + alignment.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Text[lines=%s, alignment=%s]", lines, alignment);
    }
}
