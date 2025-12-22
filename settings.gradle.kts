rootProject.name = "glimt-parent"

include(
    "glimt-core",
    "glimt-widgets",
    "glimt-jline",
    "glimt-tui",
    "glimt-picocli",
    "glimt-toolkit"
)

File(settingsDir, "demos").listFiles()?.forEach {
    if (it.isDirectory) {
        include("demos:${it.name}")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")