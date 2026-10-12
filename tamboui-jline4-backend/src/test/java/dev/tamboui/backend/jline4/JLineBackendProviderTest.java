/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.backend.jline4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.tamboui.terminal.BackendProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class JLineBackendProviderTest {

    @Test
    @DisplayName("the provider is registered as a service and named jline4")
    void registeredAsJline4() {
        List<String> names = new ArrayList<>();
        for (BackendProvider provider : ServiceLoader.load(BackendProvider.class)) {
            names.add(provider.name() + "=" + provider.getClass().getName());
        }

        assertThat(names).contains("jline4=" + JLineBackendProvider.class.getName());
    }

    @Test
    @DisplayName("building the terminal does not write JLine's terminal mode queries")
    void noModeProbe() throws IOException {
        // The probe only runs on a terminal backed by a pty, so this test opens one through
        // JLine's JNI provider, with the same builder settings as the system terminal.
        assumeFalse(System.getProperty("os.name").startsWith("Windows"), "needs a pty");
        PipedOutputStream keyboard = new PipedOutputStream();
        ByteArrayOutputStream screen = new ByteArrayOutputStream();

        Terminal terminal = JLineBackend.systemTerminalBuilder()
                .system(false)
                .provider("jni")
                .streams(new PipedInputStream(keyboard), screen)
                .type("xterm-256color")
                .size(Size.of(80, 24))
                .build();
        String written = new String(screen.toByteArray(), StandardCharsets.UTF_8);
        keyboard.close();
        terminal.close();

        // DECRQM for mode 2027 and the DA1 request that ends JLine's probe
        assertThat(written).doesNotContain("\u001b[?2027$p", "\u001b[c");
    }
}
