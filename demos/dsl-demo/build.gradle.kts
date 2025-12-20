plugins {
    id("io.github.jratatui.demo-project")
}

description = "Demo showcasing the DSL module with Widget Playground"

dependencies {
    implementation(projects.jratatuiDsl)
}

application {
    mainClass.set("io.github.jratatui.demo.DslDemo")
}
