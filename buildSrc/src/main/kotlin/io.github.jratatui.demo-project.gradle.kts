import gradle.kotlin.dsl.accessors._d7c1cb8291fcf7e869bfba85a0dc6ae2.java

plugins {
    id("io.github.jratatui.java-base")
    application
    id("org.graalvm.buildtools.native")
}

dependencies {
    implementation(project(":jratatui-core"))
    implementation(project(":jratatui-widgets"))
    implementation(project(":jratatui-jline"))
}

tasks.named<JavaExec>("run") {
    enabled = false
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}


graalvmNative {
    binaries {
        named("main") {
            sharedLibrary = false

            buildArgs.addAll(
                "--no-fallback",
                "--initialize-at-build-time=org.jline",
                "--initialize-at-run-time=org.jline.nativ",
                "--allow-incomplete-classpath",
                "-H:+ReportExceptionStackTraces"
            )

            // JLine requires access to terminal
            jvmArgs.addAll(
                "--add-opens", "java.base/java.lang=ALL-UNNAMED",
                "--add-opens", "java.base/java.io=ALL-UNNAMED"
            )
        }
    }

    toolchainDetection.set(false)
}
