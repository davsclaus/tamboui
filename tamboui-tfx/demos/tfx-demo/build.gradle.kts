plugins {
    id("dev.tamboui.demo-project")
}

description = "Demo TUI application showcasing TamboUI effects"

dependencies {
    implementation(projects.tambouiTfx)
    implementation(projects.tambouiTui)
}

application {
    mainClass.set("dev.tamboui.demo.TFXEffectsDemo")
}

demo {
    displayName = "TamboUI TFX Advanced Effects Demo"
    tags = setOf("tfx", "effects", "animation", "progress", "widgets")
}