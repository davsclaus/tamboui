plugins {
    id("io.github.jratatui.java-library")
}

description = "Fluent DSL for building TUI applications with JRatatui"

dependencies {
    api(projects.jratatuiCore)
    api(projects.jratatuiWidgets)
    api(projects.jratatuiTui)
}
