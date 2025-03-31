package model

import exceptions.InvalidOperandException
import util.Register

class Operand(private val input: String) {
    private val literalIndex: Int
    val dereferenceCount: Int

    init {
        // Überprüfen, ob der String dem erwarteten Muster entspricht
        val matchResult = Regex("""^\**(\d+)$""").matchEntire(input)
            ?: throw InvalidOperandException(input)

        // Zähle die Anzahl der Dereferenzierungen und extrahiere die Zahl
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