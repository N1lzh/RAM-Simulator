package util

/**
 * Configuration class for the RAM simulator.
 * See [config.properties](src/main/kotlin/config.properties) for more documentation.
 */
data class Config(
    val maxInstructions: Int,
    val logging: Boolean,
    val debug: Boolean,
    val timeout: Long,
    val numOfLinesLogged: Int,
)