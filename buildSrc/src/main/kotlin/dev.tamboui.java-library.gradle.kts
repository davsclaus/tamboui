plugins {
    `java-library`
    id("dev.tamboui.publishing")
    id("ru.vyarus.animalsniffer")
}

dependencies {
    val libs = versionCatalogs.named("libs")
    testImplementation(platform(libs.findLibrary("junit.bom").orElseThrow()))
    testImplementation(libs.findBundle("testing").orElseThrow())
    signature(libs.findLibrary("sniffer18-signature").orElseThrow()) {
        artifact { type = "signature" }
    }
}
