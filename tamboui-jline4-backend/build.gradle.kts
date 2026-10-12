plugins {
    id("dev.tamboui.java-library")
}

description = "JLine 4 backend for TamboUI TUI library"

dependencies {
    api(projects.tambouiCore)
    api(libs.jline4)

    // EventParser, to test that typed keys, mouse reports and pastes arrive as events
    testImplementation(projects.tambouiTui)
}

// JLine 4 requires Java 11
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 11
}

tasks.withType<Test> {
    // A test opens a pty through JLine's native library
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
