plugins {
    id("io.github.jratatui.demo-project")
}

description = "Demo showcasing PicoCLI integration with JRatatui"

dependencies {
    implementation(projects.jratatuiPicocli)
    annotationProcessor(libs.picocli.codegen)
}

application {
    mainClass.set("io.github.jratatui.demo.PicoCLIDemo")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "-Aproject=${project.group}/${project.name}",
        "-Xlint:-processing"  // Suppress annotation processor warnings
    ))
}
