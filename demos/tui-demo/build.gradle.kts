plugins {
    id("io.github.jratatui.demo-project")
}

description = "Demo showcasing the TuiRunner framework"

dependencies {
    implementation(projects.jratatuiTui)
}

application {
    mainClass.set("io.github.jratatui.demo.TuiDemo")
}
