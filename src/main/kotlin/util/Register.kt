package util

object Register {
    private val registers: MutableMap<Int, Int> = mutableMapOf(0 to 0)

    fun get(index: Int) = registers.getOrDefault(index, 0)

    fun set(index: Int, value: Int) = registers.set(index, value)

    fun getAccumulator() = registers.getOrDefault(0, 0)

    fun setAccumulator(value: Int) = registers.set(0, value)

    fun storeAccumulator(index: Int) = registers.set(index, getAccumulator())

    override fun toString() = registers.toString()
}