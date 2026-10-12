import dev.tamboui.backend.jline4.JLineBackendProvider;
import dev.tamboui.terminal.BackendProvider;

/**
 * JLine 4 backend for TamboUI TUI library.
 * <p>
 * This module provides a terminal backend implementation using JLine 4,
 * enabling TamboUI applications to run in standard terminals on Java 11 and later.
 */
@SuppressWarnings({"requires-transitive-automatic", "requires-automatic"})
module dev.tamboui.jline_four.backend {
    requires transitive dev.tamboui.core;
    requires transitive org.jline;

    exports dev.tamboui.backend.jline4;

    provides BackendProvider with JLineBackendProvider;
}
