plugins {
}

allprojects {
    group = "com.lbell91.waypoint"
    version = "0.1.0-SNAPSHOT"
    repositories {
        mavenCentral()
    }
}

subprojects {
}

tasks.register("ci") {
    group = "verification"
    description = "Runs all checks in all subprojects."
    dependsOn(subprojects.map { "${it.path}:build" })
}
