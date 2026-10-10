plugins {
    id("dev.tamboui.demo-project")
}

description = "Demo showcasing Paragraph widget"

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.testing)
}

demo {
    tags = setOf("paragraph", "block", "text", "wrapping", "scrolling")
}

application {
    mainClass.set("dev.tamboui.demo.ParagraphDemo")
}

