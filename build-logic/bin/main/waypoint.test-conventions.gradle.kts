import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    java
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

dependencies {
    add("testImplementation", libs.findLibrary("junit-jupiter").orElseThrow())
    add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").orElseThrow())
}