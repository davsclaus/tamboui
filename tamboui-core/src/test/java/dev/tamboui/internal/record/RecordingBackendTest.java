/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.internal.record;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.buffer.DiffResult;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.TestBackend;

import static org.assertj.core.api.Assertions.assertThat;

class RecordingBackendTest {

    private static final Rect AREA = new Rect(0, 0, 10, 1);

    @TempDir
    Path tempDir;

    @AfterEach
    void tearDown() {
        RecordingConfig.clearActive();
    }

    @Test
    @DisplayName("A screen drawn within the frame interval of the previous frame ends the cast")
    void lastScreenDrawnWithinFrameIntervalIsRecorded() throws IOException {
        Path cast = tempDir.resolve("out.cast");
        // At 1 fps the second draw lands in the same frame interval as the first one
        RecordingBackend backend = new RecordingBackend(new TestBackend(10, 1), RecordingConfig.of(cast, 1, 60000, 10, 1));
        Buffer empty = Buffer.empty(AREA);
        Buffer saving = screen("Saving...");
        Buffer saved = screen("Saved");

        draw(backend, empty, saving);
        draw(backend, saving, saved);
        backend.close();

        List<String> outputs = frameOutputs(cast);
        assertThat(outputs).hasSize(2);
        assertThat(outputs.get(0)).contains("Saving...");
        assertThat(outputs.get(1)).contains("Saved").doesNotContain("Saving");
    }

    @Test
    @DisplayName("No frame is added when every draw was captured")
    void noExtraFrameWhenEveryDrawWasCaptured() throws IOException {
        Path cast = tempDir.resolve("out.cast");
        RecordingBackend backend = new RecordingBackend(new TestBackend(10, 1), RecordingConfig.of(cast, 1, 60000, 10, 1));

        draw(backend, Buffer.empty(AREA), screen("Saved"));
        backend.close();

        List<String> outputs = frameOutputs(cast);
        assertThat(outputs).hasSize(1);
        assertThat(outputs.get(0)).contains("Saved");
    }

    private static Buffer screen(String text) {
        Buffer buffer = Buffer.empty(AREA);
        buffer.setString(0, 0, text, Style.EMPTY);
        return buffer;
    }

    private static void draw(RecordingBackend backend, Buffer previous, Buffer next) throws IOException {
        DiffResult diff = new DiffResult();
        previous.diff(next, diff);
        backend.draw(diff);
        diff.clear();
    }

    /** The output of each frame event in the cast, leaving out the empty event that pads the duration. */
    private static List<String> frameOutputs(Path cast) throws IOException {
        List<String> outputs = new ArrayList<>();
        List<String> lines = Files.readAllLines(cast, StandardCharsets.UTF_8);
        for (String line : lines.subList(1, lines.size())) {
            String output = line.substring(line.indexOf("\"o\", \"") + 6, line.lastIndexOf('"'));
            if (!output.isEmpty()) {
                outputs.add(output);
            }
        }
        return outputs;
    }
}
