package net.taobits.paramecium


typealias Program = List<Command>

class ProgrammProcessor(val program: Program) {
    var programCounter = 0
    var ticks = 0

    fun execute(paramecium: Paramecium, debug: Boolean = false) {
        programCounter = 0
        ticks = 0
        while (programCounter in 0 until program.size && paramecium.energy > 0)
        {
            program[programCounter].execute(this, paramecium)
            paramecium.consumeEnergy()
            ticks++
            if (debug) {
                println("ticks=$ticks pc=$programCounter")
                println(paramecium.world)
                println()
            }
            programCounter++
        }
    }

    fun goto(line: Int) {
        programCounter = line
    }

    override fun toString() = program.toFormattedString()
}

fun Program.toFormattedString() = mapIndexed { line, cmd ->  "$line $cmd" }.joinToString("\n")