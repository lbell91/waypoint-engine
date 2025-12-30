plugins {
    id("waypoint.java-base-conventions")
    id("waypoint.test-conventions")
    `java-library`
}

java {
    withSourcesJar()
    withJavadocJar()
}
