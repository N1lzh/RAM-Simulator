package model

import exceptions.InvalidOperandException
import util.Register

/**
 * An operand for the RAM. Can be a literal or a reference to a register.
 * Dereferencing is done by prefixing the operand with an arbitrary number of asterisks.
 *
 * @param input The input string to parse.
 */
class Operand(private val input: String) {
    private val literalIndex: Int
    val dereferenceCount: Int

    init {
        // Check if the input matches the pattern *<number>
        val matchResult = Regex("""^\**(\d+)$""").matchEntire(input)
            ?: throw InvalidOperandException(input)

        this.dereferenceCount = input.count { it == '*' }
        this.literalIndex = matchResult.groups[1]!!.value.toInt()
    }

    val index: Int
        get() = dereference(dereferenceCount, literalIndex)

    val value: Int
        get() = if (dereferenceCount == 0) index else Register.get(dereference(dereferenceCount, index))


    private fun dereference(count: Int, index: Int): Int {
        return if (count <= 1) {
            index
        } else {
            dereference(count - 1, Register.get(index))
        }
    }

    override fun toString() = input
}