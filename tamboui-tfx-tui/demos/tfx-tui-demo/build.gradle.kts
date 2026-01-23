plugins {
    id("dev.tamboui.demo-project")
}

description = "Demo showcasing TFX integration with TuiRunner"

dependencies {
    implementation(projects.tambouiTfxTui)
}

application {
    mainClass.set("dev.tamboui.demo.TfxTuiDemo")
}

demo {
    displayName = "TFX TUI Integration Demo"
    tags = setOf("tfx", "effects", "animation", "tui")
    // in docs, display the demo in the TFX section
    module = "TFX"
}
