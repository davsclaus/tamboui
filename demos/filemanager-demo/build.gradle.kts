plugins {
    id("dev.tamboui.demo-project")
}

description = "Two-panel file manager demo showcasing MVC architecture"

demo {
    displayName = "File Manager"
    tags = setOf("toolkit", "list", "panel", "text", "application", "mvc", "interactive")
}


java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(projects.tambouiToolkit)
    implementation(projects.tambouiImage)
}

application {
    mainClass.set("dev.tamboui.demo.filemanager.FileManagerDemo")
}