plugins {
    id("waypoint.java-base-conventions")
    id("waypoint.test-conventions")
    application
}

application {
    // Allow override via -PmainClass=...
    val mainClassProp = providers.gradleProperty("mainClass").orNull
    if (!mainClassProp.isNullOrBlank()) {
        mainClass.set(mainClassProp)
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

tasks.withType<Zip>().configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
