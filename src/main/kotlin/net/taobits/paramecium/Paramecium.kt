package net.taobits.paramecium

typealias Program = List<Command>

class Paramecium(var coord: Coord = Coord(0, 0), var program: Program = emptyList()) {
    lateinit var world: World
    var ip = 0

    fun start() {
        while (ip < program.size) program[ip].execute(this)
    }

    fun move(direction: Direction) {
        val nextCoord = changeCoord(direction, coord)
        val movePossible = world[nextCoord] != Something.WALL
        if (movePossible) {
            coord = nextCoord
            eat()
        }
        ip++
    }

    fun condition(direction: Direction, what: Something) { // Skip instruction if cell in direction is not what
        val senseCoord = changeCoord(direction, coord)
        if (world[senseCoord] != what) ip++
        ip++
    }

    fun goto(steps: Int) {
        ip += steps
        ip++
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
        if (world[coord] == Something.FOOD) world[coord] = Something.EMPTY
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
