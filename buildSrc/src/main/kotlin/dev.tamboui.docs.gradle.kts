plugins {
    id("org.asciidoctor.jvm.convert")
}

repositories {
    mavenCentral()
}

tasks.asciidoctor {
    setSourceDir(file("src/docs/asciidoc"))
    setBaseDir(file("src/docs/asciidoc"))
    setOutputDir(layout.buildDirectory.dir("docs"))

    // Copy theme resources
    resources {
        from("src/theme") {
            into("_static")
        }
    }

    attributes(
        mapOf(
            "source-highlighter" to "highlight.js",
            "highlightjsdir" to "_static/highlight",
            "highlightjs-theme" to "github-dark",
            "stylesheet" to "_static/tamboui.css",
            "linkcss" to true,
            "icons" to "font",
            "toc" to "left",
            "toclevels" to 3,
            "sectanchors" to true,
            "sectlinks" to true,
            "idprefix" to "",
            "idseparator" to "-",
            "source-indent" to 0,
            "tabsize" to 4,
            // Docinfo for theme toggle and navigation
            "docinfo" to "shared",
            // Project info
            "project-version" to project.version,
            "project-name" to "TamboUI",
            "github-repo" to "tamboui/tamboui"
        )
    )
}
