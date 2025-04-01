package service

import exceptions.InvalidInstructionException
import exceptions.InvalidOperandException
import exceptions.SimulatorException
import model.Command
import model.Instruction
import model.Operand
import util.Register

class RAMSimulator(
    private val input: List<Int> = listOf(),
    raw: List<String>,
    private val logging: Boolean = false,
    private val timeout: Long = 1
) {
    private val instructions: List<Command> = raw.mapIndexed { index, it -> compileInstruction(it, index) }
    var executedInstructions: MutableList<Command> = mutableListOf()
    val output = mutableListOf<Int>()

    private var instructionPointer = 1
    private var inputPointer = 0

    private fun compileInstruction(input: String, index: Int): Command {
        val parts = input.split(Regex("\\s+")).toMutableList()
        if (parts.size < 2) {
            parts.add("0")
        }

        val instruction: Instruction
        try {
            instruction = Instruction.fromString(parts[0])
        } catch (e: InvalidInstructionException) {
            throw SimulatorException("Invalid Instruction ${e.message} at line $instructionPointer")
        }


        val operand: Operand
        try {
            operand = Operand(parts[1])
        } catch (e: InvalidOperandException) {
            throw SimulatorException("Invalid Operand ${e.message} at line $instructionPointer")
        }

        return Command(instruction, operand, index + 1, this)
    }

    fun execute() {
        val command = instructions.getOrNull(instructionPointer - 1)
            ?: throw SimulatorException("No instruction found at line $instructionPointer")

        executedInstructions.add(command)

        if(logging) {
            var linesPrinted = 0

            println("\u001B[33m$Register")
            println()
            linesPrinted += 2

            repeat(5) {
                val i = executedInstructions.size  - 5 + it
                try {
                    val color = when (it) {
                        4 -> "\u001B[34m"
                        3 -> "\u001B[32m"
                        else -> "\u001B[0m"
                    }

                    println("$color${executedInstructions[i]}")
                    linesPrinted++
                } catch (_: IndexOutOfBoundsException) { }
            }

            Thread.sleep(timeout * 1000)
            repeat(linesPrinted) {
                print("\u001B[1A") // Cursor eine Zeile nach oben bewegen
                print("\u001B[2K") // Zeile löschen
            }
        }

        command.execute()

        instructionPointer++
    }

    fun isStopped() = instructionPointer == 0

    fun read(input: Operand) {
        if (input.dereferenceCount > 0) throw IllegalArgumentException("No dereferencing allowed for READ")
        Register.set(input.index, this.input[inputPointer++])
    }

    fun write(input: Operand) {
        if (input.dereferenceCount > 0) throw IllegalArgumentException("No dereferencing allowed for WRITE")
        output.add(Register.get(input.index))
    }

    fun load(input: Operand) {
        Register.setAccumulator(input.value)
    }

    fun store(input: Operand) {
        Register.storeAccumulator(input.index)
    }

    fun mathOperation(input: Operand, operation: (Int, Int) -> Int) {
        val value = operation(Register.getAccumulator(), input.value)
        Register.setAccumulator(value)
    }

    fun goto(input: Operand) {
        if (input.dereferenceCount > 0) throw IllegalArgumentException("No dereferencing allowed for GOTO")
        instructionPointer = input.value - 1
    }

    fun jump(input: Operand, condition: (Int) -> Boolean) {
        if (input.dereferenceCount > 0) throw IllegalArgumentException("No dereferencing allowed for JUMP")
        if (condition(Register.getAccumulator())) {
            instructionPointer = input.index - 1
        }
    }
}