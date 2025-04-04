package util

import model.Command
import model.Register

/**
 * A class that represents the state of the Simulator.
 * This class is used to store the state of the Simulator at a given point in time.
 */
data class SimulationState (
    val instructionPointer: Int,
    val inputPointer: Int,
    val output: List<Int>,
    val registers: Map<Int, Int>,
    val currentCommand: Command,
) {
    companion object {
        /**
         * Creates a new SimulatorState from the current state of the simulator.
         *
         * @param instructionPointer The current instruction pointer.
         * @param inputPointer The current input pointer.
         * @param output The current output.
         *
         * @return A new SimulatorState object.
         */
        fun fromCurrentState(
            instructionPointer: Int,
            inputPointer: Int,
            output: List<Int>,
            currentCommand: Command
        ) = SimulationState(
            instructionPointer = instructionPointer,
            inputPointer = inputPointer,
            output = output.toList(),
            registers = Register.registers.toMap(),
            currentCommand = currentCommand,
        )
    }

    override fun toString(): String {
        return "{instructionPointer=$instructionPointer, " +
                "inputPointer=$inputPointer, " +
                "output=$output, " +
                "registers=$registers, " +
                "currentCommand=$currentCommand}"
    }
}