/**
 * Precompiled [waypoint.java-application-conventions.gradle.kts][Waypoint_java_application_conventions_gradle] script plugin.
 *
 * @see Waypoint_java_application_conventions_gradle
 */
public
class Waypoint_javaApplicationConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Waypoint_java_application_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
