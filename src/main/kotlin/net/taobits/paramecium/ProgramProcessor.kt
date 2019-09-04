package net.taobits.paramecium


typealias Program = List<Command>

class ProgrammProcessor(val program: Program) {
    var programCounter = 0

    fun execute(paramecium: Paramecium, debug: Boolean = false) {
        programCounter = 0
        while (programCounter in 0 until program.size && paramecium.energy > 0)
        {
            program[programCounter].execute(this, paramecium)
            paramecium.consumeEnergy()
            if (debug) {
                println(paramecium.world)
                println()
            }
            programCounter++
        }
    }

    fun goto(line: Int) {
        programCounter = line
    }

    override fun toString() = program.toString()
}

fun Program.toString() = mapIndexed { line, cmd ->
    println("$line $cmd")
}