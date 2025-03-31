package model

import exceptions.InvalidInstructionException

/**
 * An instruction for the RAM.
 */
enum class Instruction {
    /**
     * Reads the value under the read head and writes it to the specified memory location n.
     * Moves the read head one position to the right.
     */
    READ,
    /**
     * Writes the value from the memory location n to the current position of the write head.
     * Moves the write head one position to the right.
     */
    WRITE,
    /**
     * Loads the value specified by the operator into the accumulator.
     */
    LOAD,
    /**
     * Stores the value in the accumulator into the memory location specified by the operator.
     */
    STORE,
    /**
     * Adds the value specified by the operator to the value in the accumulator.
     */
    ADD,
    /**
     * Subtracts the value specified by the operator from the value in the accumulator.
     */
    SUB,
    /**
     * Multiplies the value specified by the operator with the value in the accumulator.
     */
    MULT,
    /**
     * Divides the value in the accumulator by the value specified by the operator.
     */
    DIV,
    /**
     * Jumps to the instruction in line n.
     */
    GOTO,
    /**
     * Jumps to the instruction in line n if the value in the accumulator is zero.
     */
    JZ,
    /**
     * Jumps to the instruction in line n if the value in the accumulator is greater than zero.
     */
    JGTZ,
    /**
     * Stops the execution by jumping to line 0.
     */
    HALT;

    companion object {
        /**
         * Returns the instruction corresponding to the given string.
         * @param instruction the string representation of the instruction
         * @return the corresponding model.Instruction enum value
         * @throws InvalidInstructionException if the instruction is not valid
         */
        fun fromString(instruction: String): Instruction {
            return entries.firstOrNull { it.name.equals(instruction, ignoreCase = true) }
                ?: throw InvalidInstructionException(instruction)
        }
    }
}