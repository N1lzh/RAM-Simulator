package service

import exceptions.InvalidInstructionException
import exceptions.InvalidOperandException
import exceptions.SimulatorException
import model.Command
import util.Config
import model.Instruction
import model.Operand
import model.Register
import util.SimulationState
import kotlin.system.exitProcess

/**
 * A simulator for the RAM.
 *
 * @param input The input values to use.
 * @param raw The raw instructions to compile.
 * @param config The configuration for the simulator.
 */
class RAMSimulator(
    private val input: List<Int> = listOf(),
    raw: List<String>,
    private val config: Config,
) {
    private val instructions: List<Command> = raw.mapIndexed { index, it -> compileInstruction(it, index) }
    private var stateHistory: MutableList<SimulationState> = mutableListOf()
    private val debugger = Debugger()

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

    /**
     * Executes the next instruction at the position of the instruction pointer.
     */
    fun execute() {
        val command = instructions.getOrNull(instructionPointer - 1)
            ?: throw SimulatorException("No instruction found at line $instructionPointer")

        stateHistory.add(SimulationState.fromCurrentState(instructionPointer, inputPointer, output, command))

        if (stateHistory.size > config.maxInstructions)
            throw SimulatorException("Number of executed instructions exceeded limit: ${config.maxInstructions}")

        if (config.logging) debugger.displayAndProcess()

        stateHistory.last().currentCommand.execute()

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

    /*
     * TODO: Refactor and move this to a separate class (maybe)
     *  - especially the suspendUntil and rollback methods need some refactoring
     */
    inner class Debugger {
        private val sb = StringBuilder()
        private var suspendedUntil = -1

        fun displayAndProcess() {
            clearLines()

            if (suspendedUntil > stateHistory.last().currentCommand.line || suspendedUntil == 0) return else suspendedUntil = -1

            sb.appendLine("\u001B[33m$Register").appendLine()

            for (i in (stateHistory.size - config.numOfLinesLogged until stateHistory.size)) {
                try {
                    val color = when (i - (stateHistory.size - config.numOfLinesLogged)) {
                        config.numOfLinesLogged - 1 -> "\u001B[34m"
                        config.numOfLinesLogged - 2 -> "\u001B[32m"
                        else -> "\u001B[0m"
                    }

                    sb.appendLine("$color${stateHistory[i].currentCommand}")
                } catch (_: IndexOutOfBoundsException) {
                    sb.appendLine()
                }
            }

            sb.appendLine()

            if(config.debug) {
                sb.appendLine("\u001B[0mPress Enter to continue or input a command... For a list of commands, type 'help'")
            } else {
                Thread.sleep(config.timeout * 1000)
            }

            print(sb.toString())
            val commandInput = if (config.debug) readln() else ""

            clearLines()
            parseCommand(commandInput)
        }

        private fun clearLines() {
            print("\u001B[H\u001B[2J") // Clear the entire screen
            sb.clear()
        }

        private fun parseCommand(input: String) {

            val parts = input.split(Regex("\\s+")).toMutableList()
            when (parts[0].lowercase()) {
                "help" -> help()
                "exit" -> exitProcess(0)
                "continue" -> suspendUntil(0)
                "reset" -> rollback(0)
                "states" -> states()
                "rollback" -> rollback(parts[1].toIntOrNull())
                "suspenduntil" -> suspendUntil(parts[1].toIntOrNull())
                else -> return
            }
        }

        private fun help() {
            sb.appendLine("Available commands:")
            sb.appendLine("help - Show this help message")
            sb.appendLine("exit - Exit the simulator")
            sb.appendLine("continue - Continues the simulator until the end without logging")
            sb.appendLine("reset - Reset the simulator")
            sb.appendLine("states - Show the previous states with their indexes")
            sb.appendLine("rollback - Rollback to a previous state by index")
            sb.appendLine("\nPress Enter to exit...")

            print(sb.toString())
            readln()

            displayAndProcess()
        }

        private fun states() {
            sb.appendLine("Previous states:")
            stateHistory.forEachIndexed { index, state ->
                sb.appendLine("$index: $state")
            }
            sb.appendLine("\nPress Enter to exit...")

            print(sb.toString())
            readln()

            displayAndProcess()
        }

        private fun rollback(index: Int?) {
            if (index == null || index < 0 || index >= stateHistory.size) {
                sb.appendLine("Invalid index: $index")
                sb.appendLine("\nPress Enter to exit...")

                print(sb.toString())
                readln()

                displayAndProcess()

                return
            }

            val state = stateHistory[index]
            stateHistory = stateHistory.subList(0, index + 1)
            instructionPointer = state.instructionPointer
            inputPointer = state.inputPointer
            output = state.output.toMutableList()
            Register.rollback(state.registers)

            sb.appendLine("Rolled back to state $index")
            sb.appendLine("Current state: $state")
            sb.appendLine("\nPress Enter to exit...")

            print(sb.toString())
            readln()

            displayAndProcess()
        }

        private fun suspendUntil(index: Int?) {
            val line = instructions.getOrNull((index?.minus(1)) ?: -1)
            if (line == null && index != 0) {
                sb.appendLine("Invalid line: $index")
                sb.appendLine("\nPress Enter to exit...")

                print(sb.toString())
                readln()

                displayAndProcess()

                return
            }

            sb.appendLine("Are you sure you want to suspend until line $index? (y/n)")
            println(sb.toString())
            val answer = readln().lowercase()

            sb.clear()

            if (answer.startsWith("y")) {
                suspendedUntil = index ?: -1
                sb.appendLine("Suspended until line $index")
            }

            sb.appendLine("\nPress Enter to exit...")

            print(sb.toString())
            readln()

            displayAndProcess()
        }
    }


}