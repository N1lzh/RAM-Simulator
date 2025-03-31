import java.nio.file.Files
import java.nio.file.Paths
import service.RAMSimulator

private fun readAllLines(filePath: String): List<String> {
    return Files.readAllLines(Paths.get(filePath))
}

fun main() {
    // Hier kann man den filepath angeben
    val filePath = "src/main/kotlin/faculty.txt"
    val lines = readAllLines(filePath)
    // Das hier ist das Eingabeband
    val input = listOf(4)

    val simulator = RAMSimulator(input, lines)

    while (!simulator.isStopped()) {
        simulator.execute()
    }

    // Ausgabe von 4! ist 24
    println(simulator.output)
}