package model

import service.RAMSimulator

/**
 * Represents a command in the RAM simulator. A command is composed of an instruction and an operand.
 * The command is executed on the an instance of a RAM simulator.
 *
 * @param instruction the instruction to execute.
 * @param operand the operand to use in the instruction.
 * @param line the line number of the command.
 * @param simulator the RAM simulator to execute the command on.
 */
data class Command(
    val instruction: Instruction,
    val operand: Operand,
    val line: Int,
    private val simulator: RAMSimulator
) {
    fun execute() {
        when (this.instruction) {
            Instruction.READ -> simulator.read(operand)
            Instruction.WRITE -> simulator.write(operand)
            Instruction.LOAD -> simulator.load(operand)
            Instruction.STORE -> simulator.store(operand)
            Instruction.ADD -> simulator.mathOperation(operand) { a, b -> a + b }
            Instruction.SUB -> simulator.mathOperation(operand) { a, b -> a - b }
            Instruction.MULT -> simulator.mathOperation(operand) { a, b -> a * b }
            Instruction.DIV -> simulator.mathOperation(operand) { a, b -> a / b }
            Instruction.GOTO -> simulator.goto(operand)
            Instruction.JZ -> simulator.jump(operand) { it == 0 }
            Instruction.JGTZ -> simulator.jump(operand) { it > 0 }
            Instruction.HALT -> simulator.goto(operand)
        }
    }

    override fun toString() = "$line: $instruction $operand"
}