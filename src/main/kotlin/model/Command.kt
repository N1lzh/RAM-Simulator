package model

import service.RAMSimulator

data class Command(
    val instruction: Instruction,
    val operand: Operand,
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

    override fun toString() = "$instruction $operand"
}