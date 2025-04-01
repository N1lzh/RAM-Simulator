package util

/**
 * A register that stores integers. The register at index 0 is called the accumulator.
 * The Register is a singleton.
 */
object Register {
    private val registers: MutableMap<Int, Int> = mutableMapOf(0 to 0)

    /**
     * Gets the value at the specified index.
     *
     * @param index the index to get the value from.
     * @return the value at the specified index.
     */
    fun get(index: Int) = registers.getOrDefault(index, 0)

    /**
     * Sets the value at the specified index.
     *
     * @param index the index to set the value at.
     * @param value the value to set.
     */
    fun set(index: Int, value: Int) = registers.set(index, value)

    /**
     * Gets the value of the accumulator.
     *
     * @return the value of the accumulator.
     */
    fun getAccumulator() = registers.getOrDefault(0, 0)

    /**
     * Sets the value of the accumulator.
     *
     * @param value the value to set.
     */
    fun setAccumulator(value: Int) = registers.set(0, value)

    /**
     * Stores the value of the accumulator at the specified index.
     *
     * @param index the index to store the accumulator value at.
     */
    fun storeAccumulator(index: Int) = registers.set(index, getAccumulator())

    override fun toString() = registers.toString()
}