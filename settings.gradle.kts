rootProject.name = "waypoint-engine"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(
    "waypoint-core",
    "waypoint-persist",
    "waypoint-api"
)
