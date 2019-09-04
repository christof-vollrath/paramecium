package net.taobits.paramecium

import java.lang.IllegalStateException
import java.util.*

abstract class Command { abstract fun execute(processor: ProgrammProcessor, paramecium: Paramecium) }
class Move(val direction: Direction) : Command() {
    override fun execute(processor: ProgrammProcessor, paramecium: Paramecium) = paramecium.move(direction)
    override fun toString() = "Move $direction"
}
class Sense(val direction: Direction, val what: Something) : Command() {
    override fun execute(processor: ProgrammProcessor, paramecium: Paramecium) {
        val sensed = paramecium.sense(direction, what)
        if (! sensed) processor.goto(1) // Skip next command
    }
    override fun toString() = "Condition $direction $what"
}
class Goto(val line: Int) : Command() {
    override fun execute(processor: ProgrammProcessor, paramecium: Paramecium) = processor.goto(line - 1)
        // Subtract 1 because program counter will be incremented after each command execution
    override fun toString() = "Goto $line"
}

enum class Direction { NORTH, EAST, SOUTH, WEST  }

const val SEED = 123L

object RandomCommandGenerator {
    private val randomGenerator = Random(SEED)
    private fun createRandomDirection(): Direction {
        return when(val r = randomGenerator.nextInt(4)) {
            0 -> Direction.NORTH
            1 -> Direction.EAST
            2 -> Direction.SOUTH
            3 -> Direction.WEST
            else -> throw IllegalStateException("Random generator for direction returned unexpected value $r")
        }
    }
    private fun createRandomSomething(): Something {
        return when(val r = randomGenerator.nextInt(3)) {
            0 -> Something.EMPTY
            1 -> Something.WALL
            2 -> Something.FOOD
            else -> throw IllegalStateException("Random generator for something returned unexpected value $r")
        }
    }
    private fun createRandomGotoSteps(gotoRange: Int): Int =randomGenerator.nextInt(gotoRange) + 1

    fun createRandomCommand(gotoRange: Int): Command {
        return when(val r = randomGenerator.nextInt(3)) {
            0 -> Move(createRandomDirection())
            1 -> Sense(createRandomDirection(), createRandomSomething())
            2 -> Goto(createRandomGotoSteps(gotoRange))
            else -> throw IllegalStateException("Random generator for command returned unexpected value $r")
        }
    }
}