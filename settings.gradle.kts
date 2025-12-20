rootProject.name = "jratatui-parent"

include(
    "jratatui-core",
    "jratatui-widgets",
    "jratatui-jline",
    "jratatui-tui",
    "jratatui-picocli",
    "jratatui-dsl"
)

File("demos").listFiles()?.forEach {
    if (it.isDirectory) {
        include("demos:${it.name}")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")