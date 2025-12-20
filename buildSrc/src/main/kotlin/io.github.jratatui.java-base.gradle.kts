import org.gradle.api.tasks.compile.JavaCompile

plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    // remove warning about Java 8 support being deprecated
    options.compilerArgs.add("-Xlint:-options")
}

group = "io.github.jratatui"
