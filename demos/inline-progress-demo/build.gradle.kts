plugins {
    id("dev.tamboui.demo-project")
}

description = "Inline progress display demo (NPM/Gradle-style)"

demo {
    displayName = "Inline Progress Demo"
}

dependencies {
    implementation(projects.tambouiTui)
    implementation(projects.tambouiWidgets)
}

application {
    mainClass.set("dev.tamboui.demo.InlineProgressDemo")
}
