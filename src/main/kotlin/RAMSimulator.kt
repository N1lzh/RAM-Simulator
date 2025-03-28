/*
    * TODO:
    * - separate class for register
    * - separate class for instruction
    * - separate class for operand
 */
class RAMSimulator(
    private val input: List<Int> = listOf<Int>()
) {
    private val output = mutableListOf<Int>()
    private val registers: MutableMap<Int, Int> = mutableMapOf(0 to 0)

    private var instructionPointer = 0
    private var inputPointer = 0

    fun execute(command: String) {
        val parts = command.split(" ")
        val instruction = Instruction.fromString(parts[0])

        when (instruction) {
            Instruction.READ -> read(parts[1])
            Instruction.WRITE -> write(parts[1])
            Instruction.LOAD -> load(parts[1])
            Instruction.STORE -> store(parts[1])
            Instruction.ADD -> mathOperation(parts[1]) { a, b -> a + b }
            Instruction.SUB -> mathOperation(parts[1]) { a, b -> a - b }
            Instruction.MUL -> mathOperation(parts[1]) { a, b -> a * b }
            Instruction.DIV -> mathOperation(parts[1]) { a, b -> a / b }
            Instruction.GOTO -> goto(parts[1])
            Instruction.JZ -> jump(parts[1]) { it == 0 }
            Instruction.JGTZ -> jump(parts[1]) { it > 0 }
            Instruction.HALT -> goto(0.toString())
        }
    }

    private fun parseOperand(input: String, allowDereferencing: Boolean = true): OperandResult? {
        // Überprüfen, ob der String dem erwarteten Muster entspricht
        val matchResult = Regex("""^\**(\d+)$""").matchEntire(input)

        // Wenn der Operand nicht dem erwarteten Muster entspricht, gib null zurück
        if (matchResult == null) {
            println("Ungültiges Format: $input")
            return null
        }

        // Zähle die Anzahl der Dereferenzierungen und extrahiere die Zahl
        val dereferenceCount = input.count { it == '*' }
        val index = matchResult.groups[1]!!.value.toInt()

        // Wenn Dereferenzierungen nicht erlaubt sind und dereferenceCount größer als 0 ist, gib null zurück
        if (!allowDereferencing && dereferenceCount > 0) {
            println("Dereferenzierungen sind nicht erlaubt für Input: $input")
            return null
        }

        // Führe die rekursive Dereferenzierung durch, um den finalen Index zu erhalten
        val finalIndex = dereference(dereferenceCount, index)

        // Gib sowohl den finalen Index als auch den direkten Wert zurück
        val directValue = if (dereferenceCount > 0) input.toInt() else registers.getOrDefault(finalIndex, 0)

        return OperandResult(finalIndex, directValue)
    }

    private fun dereference(dereferenceCount: Int, index: Int): Int {
        return if (dereferenceCount == 0) {
            index
        } else {
            dereference(dereferenceCount - 1, registers.getOrDefault(index, 0))
        }
    }

    private fun read(input: String) {
        val operand = parseOperand(input, false) ?: throw IllegalArgumentException("No dereferencing allowed for READ")
        registers[operand.index] = this.input[inputPointer++]
    }

    private fun write(input: String) {
        val operand = parseOperand(input, false) ?: throw IllegalArgumentException("No dereferencing allowed for READ")
        output.add(operand.value)
    }

    private fun load(input: String) {
        val operand = parseOperand(input) ?: throw IllegalArgumentException("Unknown Problem")
        registers[0] = operand.value
    }

    private fun store(input: String) {
        val operand = parseOperand(input) ?: throw IllegalArgumentException("Unknown Problem")
        registers[operand.index] = registers.getOrDefault(0, 0)
    }

    private fun mathOperation(input: String, operation: (Int, Int) -> Int) {
        val operand = parseOperand(input) ?: throw IllegalArgumentException("Unknown Problem")
        registers[0] = operation(registers.getOrDefault(0, 0), operand.value)
    }

    private fun goto(input: String) {
        val operand = parseOperand(input, false) ?: throw IllegalArgumentException("No dereferencing allowed for GOTO")
        instructionPointer = operand.value
    }

    private fun jump(input: String, condition: (Int) -> Boolean) {
        val operand = parseOperand(input) ?: throw IllegalArgumentException("Unknown Problem")
        if (condition(registers.getOrDefault(0, 0))) {
            instructionPointer = operand.index
        }
    }
}