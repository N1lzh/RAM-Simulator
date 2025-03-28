/**
 * An instruction for the RAM.
 * Some instructions require an operand, which can be a a immediate, directly addressed or indirectly addressed value.
 * The operand is indicated by an asterisk (*), where:
 * - no asterisk refers to a literal value.
 * - an asterisk before the value refers to a memory location.
 * - two asterisks before the value refers to a memory location that is indirectly addressed by the literal value in the memory location of *n.
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
    MUL,
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
         * @return the corresponding Instruction enum value
         * @throws IllegalArgumentException if the instruction is not valid
         */
        fun fromString(instruction: String): Instruction {
            return entries.firstOrNull { it.name.equals(instruction, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid instruction: $instruction")
        }
    }


}