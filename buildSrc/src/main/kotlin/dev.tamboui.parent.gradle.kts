import dev.tamboui.build.SplitPackageCheckTask

plugins {
    base
    id("dev.tamboui.maven-central-publishing")
}

val splitPackageCheck = tasks.register<SplitPackageCheckTask>("splitPackageCheck") {
    description = "Checks for split packages across library modules"
    group = "verification"

    // Discover all modules by looking for top-level directories with build.gradle.kts
    rootDir.listFiles()
        ?.filter { it.isDirectory && file("${it.name}/build.gradle.kts").exists() }
        ?.forEach { moduleDir ->
            val sourceDir = file("${moduleDir.name}/src/main/java")
            if (sourceDir.exists()) {
                sourceSet(moduleDir.name, fileTree(sourceDir) { include("**/*.java") })
            }
        }

    reportFile.set(layout.buildDirectory.file("reports/split-packages/report.txt"))
}

tasks.named("check") {
    dependsOn(splitPackageCheck)
}
