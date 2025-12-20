plugins {
    id("io.github.jratatui.java-library")
}

description = "High-level TUI application framework for JRatatui"

dependencies {
    api(projects.jratatuiCore)
    api(projects.jratatuiWidgets)
    api(projects.jratatuiJline)
}
