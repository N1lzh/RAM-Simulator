import java.nio.file.Files
import java.nio.file.Paths
import service.RAMSimulator
import java.io.FileInputStream
import java.util.Properties

private fun readAllLines(filePath: String): List<String> {
    return Files.readAllLines(Paths.get(filePath))
}

/**
 * The main function of the program.
 *
 * @param args The arguments passed to the program. The first argument should be the path to the txt file.
 */
fun main(args: Array<String>) {
    val properties = Properties()
    FileInputStream("src/main/kotlin/config.properties").use { inputStream ->
        properties.load(inputStream)
    }

    val logging = properties.getProperty("logging").toBoolean()
    val timeout = properties.getProperty("timeout").toLong()
    val numOfLinesLogged = properties.getProperty("numOfLinesLogged").toInt()

    val filePath = if (args.isEmpty() || args[0].isEmpty()) {
        println("Please specify a path to your txt file:")
        readln()
    } else {
        args[0]
    }
    val lines = readAllLines(filePath)

    println("Please enter the input for your program, separated by commas:")
    val input = readln().split("\\s*,\\s*".toRegex()).map { e -> e.toInt() }

    println("Starting Simulator with configurations {logging = $logging, timeout = $timeout}\n")
    val simulator = RAMSimulator(
        input = input,
        raw = lines,
        logging = logging,
        timeout = timeout,
        numOfLinesLogged = numOfLinesLogged
    )

    while (!simulator.isStopped()) {
        simulator.execute()
    }

    println("\u001B[0mSimulator stopped with output \n\u001B[34m${simulator.output}")
}