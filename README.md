# RAM-Simulator
A simple RAM (Random-Access Machine) Simulator in Console written in Kotlin.

## Prerequisites
- [Kotlin Compiler](https://kotlinlang.org/docs/command-line.html)
- [Java Runtime Environment](https://www.oracle.com/java/technologies/downloads/)
- Recommended: Some kind of terminal with full control support for proper logging (e.g. WSL)

## How to use
1. Clone the repository
2. Compile the project with something like `kotlinc $(find src/main/kotlin -name "*.kt") -include-runtime -d main.jar`
3. Run the project with `kotlin -classpath main.jar MainKt`
4. Follow the instructions in the console. File paths need to be specified from the content root. Best option is to put all your program.txts into `src/main/resources`.

**NOTE:** This explains how to compile and run the project via console. You can also do all of this directly in IntelliJ IDEA.

## Config
The `config.properties` file contains some properties for you to adjust.
```properties
logging = true
timeout = 1
numOfLinesLogged = 5
```
- `logging`: If set to `true`, the program will log every instruction and the current state of the RAM
- `timeout`: The timeout in seconds for the program to wait between each log instruction and log output (only if `logging` is set to `true`)
- `numOfLinesLogged`: The number of lines that will be logged in the console (only if `logging` is set to `true`)

**Important:** Logging behaviour can vary between different console environments due to how they handle console output and control sequences.
If you need proper console logging, you should consider using a terminal with full control support, such as WSL, which can handle sequences 
such as a carriage return (`\r`).\ Most of the time, the built-in consoles in IDE's don't support these features.

## Program

You can read more about the Random-Access Machine [here](https://en.wikipedia.org/wiki/Random-access_machine).

In this implementation specific implementation, we define the program to be a list of commands. Each command has an instruction and an operand.
The instruction specifies what the command should do and the operand specifies the literal value or memory location the instruction should work with.

### Instructions
A command can have one of those instructions:
- `READ`: Reads the value under the read head and writes it to the specified memory location. After that, the read head moves one position to the right.
- `WRITE`: Writes the value of the specified memory location to the memory location under the write head. After that, the write head moves one position to the right.
- `LOAD`: Loads the value specified by the operator into the accumulator.
- `STORE`: Stores the value in the accumulator into the memory location specified by the operator.
- `ADD`: Adds the value specified by the operator to the value in the accumulator.
- `SUB`: Subtracts the value specified by the operator from the value in the accumulator.
- `MULT`: Multiplies the value specified by the operator with the value in the accumulator.
- `DIV`: Divides the value in the accumulator by the value specified by the operator.
- `GOTO`: Jumps to the instruction in line n.
- `JZ`: Jumps to the instruction in line n if the value in the accumulator is zero.
- `JGTZ`: Jumps to the instruction in line n if the value in the accumulator is greater than zero.
- `HALT`: Stops the program by jumping to line 0.

### Operand
The operand is either a literal value or a reference to a memory location. The reference to a memory location is specified by an asterisk followed by the number of the location.
Dereferencing is supported by using an arbitrary number of asterisks followed by the number of the location.

Example:
- `1` refers to the literal value 1
- `*1` refers to the value at memory location 1
- `**1` refers to the value at the memory location that is stored at memory location 1
- `***1` ...

### Example
Example Program as of `src/main/resources/faculty.txt`:
```
READ 1
LOAD *1
JGTZ 6
WRITE 0
GOTO 22
LOAD *1
STORE 2
LOAD *1
SUB 1
STORE 3
LOAD *3
JGTZ 14
GOTO 21
LOAD *2
MULT *3
STORE 2
LOAD *3
SUB 1
STORE 3
GOTO 11
WRITE 2
HALT
```


