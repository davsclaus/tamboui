plugins {
    id("io.github.jratatui.java-base")
    `maven-publish`
}

publishing {
    repositories {
        maven {
            name = "build"
            url = uri("${rootProject.layout.buildDirectory.get().asFile}/repo")
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            version = rootProject.version.toString()
        }
    }
}

