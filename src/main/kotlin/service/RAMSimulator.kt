package service

import exceptions.InvalidInstructionException
import exceptions.InvalidOperandException
import exceptions.SimulatorException
import model.Command
import model.Instruction
import model.Operand
import util.Register

/**
 * A simulator for the RAM.
 *
 * @param input The input values to use.
 * @param raw The raw instructions to compile.
 * @param logging Whether to log the state of the simulator.
 * @param timeout The timeout between logging in seconds.
 */
class RAMSimulator(
    private val input: List<Int> = listOf(),
    raw: List<String>,
    private val logging: Boolean,
    private val timeout: Long,
    private val numOfLinesLogged: Int
) {
    private val instructions: List<Command> = raw.mapIndexed { index, it -> compileInstruction(it, index) }
    private var executedInstructions: MutableList<Command> = mutableListOf()

    private var instructionPointer = 1 // instruction pointer is basically the line number
    private var inputPointer = 0 // input pointer is the index of the input list

    var output = mutableListOf<Int>() // The output values of the simulator
        private set

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

    private fun log() {
        var linesPrinted = 0

        println("\u001B[33m$Register")
        println()
        linesPrinted += 2

        for (i in (executedInstructions.size - numOfLinesLogged until executedInstructions.size)) {
            try {
                val color = when (i - (executedInstructions.size - numOfLinesLogged)) {
                    numOfLinesLogged - 1 -> "\u001B[34m"
                    numOfLinesLogged - 2 -> "\u001B[32m"
                    else -> "\u001B[0m"
                }

                println("$color${executedInstructions[i]}")
                linesPrinted++
            } catch (_: IndexOutOfBoundsException) {
                println()
                linesPrinted++
            }
        }

        Thread.sleep(timeout * 1000)
        repeat(linesPrinted) {
            print("\u001B[1A") // Move cursor one line up
            print("\u001B[2K") // Delete line
        }
    }

    /**
     * Executes the next instruction at the position of the instruction pointer.
     */
    fun execute() {
        val command = instructions.getOrNull(instructionPointer - 1)
            ?: throw SimulatorException("No instruction found at line $instructionPointer")

        executedInstructions.add(command)

        if (logging) log()

        command.execute()

        instructionPointer++
    }

    /**
     * Checks if the simulator has stopped executing. This is the case if the instruction pointer is at 0.
     */
    fun isStopped() = instructionPointer == 0

    /*
     * The following functions are the implementations of the instructions. They are more deeply explained in the
     * documentation of the model.Instruction enum.
     */

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