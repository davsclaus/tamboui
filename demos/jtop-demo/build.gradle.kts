plugins {
    id("io.github.jratatui.demo-project")
}

description = "JTop - System monitor demo using the DSL module"

dependencies {
    implementation(projects.jratatuiDsl)
}

application {
    mainClass.set("io.github.jratatui.demo.JTopDemo")
}
