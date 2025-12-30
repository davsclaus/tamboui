plugins {
    id("dev.tamboui.demo-project")
}

description = "JTop - System monitor demo using the DSL module"

dependencies {
    implementation(projects.tambouiToolkit)
    implementation(libs.oshi.core)
}

application {
    mainClass.set("dev.tamboui.demo.JTopDemo")
}
