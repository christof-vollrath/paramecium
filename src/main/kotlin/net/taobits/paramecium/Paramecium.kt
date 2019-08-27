package net.taobits.paramecium

typealias Program = List<Command>

const val INITAL_FOOD = 10
const val CONSUMPTION_PER_COMMAND = 1
const val FOOD_PER_CELL = 3

class Paramecium(var coord: Coord = Coord(0, 0), var program: Program = emptyList()) {
    lateinit var world: World
    var ip = 0
    var food: Int = INITAL_FOOD

    fun start() {
        while (ip in 0 until program.size && food > 0)
        {
            program[ip].execute(this)
        }
    }

    fun move(direction: Direction) {
        val nextCoord = changeCoord(direction, coord)
        val movePossible = world[nextCoord] != Something.WALL
        if (movePossible) {
            coord = nextCoord
            eat()
        }
        consumeFoodAndIncrementIp()
    }

    fun condition(direction: Direction, what: Something) { // Skip instruction if cell in direction is not what
        val senseCoord = changeCoord(direction, coord)
        if (world[senseCoord] != what) ip++
        consumeFoodAndIncrementIp()
    }

    fun goto(steps: Int) {
        ip += steps
        consumeFoodAndIncrementIp()
    }

    private fun changeCoord(direction: Direction, coord: Coord): Coord {
        val nextCoord = with(coord) {
            when (direction) {
                Direction.NORTH -> Coord(x, y - 1)
                Direction.EAST -> Coord(x + 1, y)
                Direction.SOUTH -> Coord(x, y + 1)
                Direction.WEST -> Coord(x - 1, y)
            }
        }
        return nextCoord
    }

    private fun eat() {
        if (world[coord] == Something.FOOD) {
            world[coord] = Something.EMPTY
            food += FOOD_PER_CELL
        }
    }

    private fun consumeFoodAndIncrementIp() {
        food -= CONSUMPTION_PER_COMMAND
        ip++
    }


}

data class Coord(val x: Int, val y: Int)

abstract class Command { abstract fun execute( paramecium: Paramecium) }
class Move(val direction: Direction) : Command() {
    override fun execute(paramecium: Paramecium) = paramecium.move(direction)
    override fun toString() = "Move $direction"
}
class Condition(val direction: Direction, val what: Something) : Command() {
    override fun execute(paramecium: Paramecium) = paramecium.condition(direction, what)
    override fun toString() = "Condition $direction $what"
}
class Goto(val steps: Int) : Command() {
    override fun execute(paramecium: Paramecium) = paramecium.goto(steps)
    override fun toString() = "Goto $steps"
}

enum class Direction { NORTH, EAST, SOUTH, WEST  }
