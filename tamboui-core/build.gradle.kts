plugins {
    id("dev.tamboui.java-library")
    `java-test-fixtures`
}

description = "Core types and abstractions for TamboUI TUI library"

dependencies {
    testFixturesApi(libs.assertj.core)
}
