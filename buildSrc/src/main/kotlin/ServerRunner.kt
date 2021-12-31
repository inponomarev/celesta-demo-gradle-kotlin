import org.gradle.api.Project
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class ServerRunner(private val project: Project) {
    fun run(): Process {
        val javaHome = System.getProperty("java.home")
        val javaPath = File(javaHome, "bin/java")
        val builder = ProcessBuilder()
            .command(
                javaPath.absolutePath,
                "-jar",
                "${project.projectDir}/build/libs/${project.rootProject.name}.jar"
            )
        val process = builder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        do {
            line = reader.readLine()
            if (line == null) {
                break
            }
            println(line)
        } while (!line!!.contains("Application is ready."))
        return process
    }
}