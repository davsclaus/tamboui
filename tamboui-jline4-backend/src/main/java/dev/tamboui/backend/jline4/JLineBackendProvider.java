/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.backend.jline4;

import java.io.IOException;

import dev.tamboui.terminal.Backend;
import dev.tamboui.terminal.BackendProvider;

/**
 * {@link BackendProvider} implementation for JLine 4.
 * <p>
 * This provider is registered via the Java {@link java.util.ServiceLoader} mechanism.
 */
public class JLineBackendProvider implements BackendProvider {

    /**
     * Creates a new JLine 4 backend provider.
     */
    public JLineBackendProvider() {
    }

    @Override
    public String name() {
        return "jline4";
    }

    @Override
    public Backend create() throws IOException {
        return new JLineBackend();
    }
}
