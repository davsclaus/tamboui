plugins {
    id("io.github.jratatui.java-library")
}

description = "PicoCLI integration for JRatatui TUI applications"

dependencies {
    api(projects.jratatuiTui)
    api(libs.picocli)
}
