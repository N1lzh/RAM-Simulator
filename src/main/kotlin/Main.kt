import java.nio.file.Files
import java.nio.file.Paths
import service.RAMSimulator
import java.io.FileInputStream
import java.util.Properties

private fun readAllLines(filePath: String): List<String> {
    return Files.readAllLines(Paths.get(filePath))
}

fun main(args: Array<String>) {
    val properties = Properties()
    FileInputStream("src/main/kotlin/config.properties").use { inputStream ->
        properties.load(inputStream)
    }

    val logging = properties.getProperty("logging").toBoolean()
    val timeout = properties.getProperty("timeout").toLong()

    if (args.size == 0) return println("Please specify a path to your txt file as first argument!")

    val filePath = args[0]
    val lines = readAllLines(filePath)

    println("Input the given input for your program. Every input needs to be separated by a comma.")
    val input = readln().split("\\s*,\\s*".toRegex()).map { e -> e.toInt() }

    println("Starting Simulator with configurations {logging = $logging, timeout = $timeout}")
    val simulator = RAMSimulator(input, lines, logging, timeout)

    while (!simulator.isStopped()) {
        simulator.execute()
    }

    // Ausgabe von 4! ist 24
    println()
    println(simulator.output)
}