package net.taobits.paramecium

typealias Program = List<Command>

const val INITAL_FOOD = 10
const val CONSUMPTION_PER_COMMAND = 1
const val FOOD_PER_CELL = 3

data class Paramecium(var coord: Coord = Coord(0, 0), var program: Program = emptyList()) {
    lateinit var world: World
    private var ip = 0
    var food: Int = INITAL_FOOD

    fun start(debug: Boolean = false) {
        ip = 0
        food = INITAL_FOOD
        while (ip in 0 until program.size && food > 0)
        {
            program[ip].execute(this)
            if (debug) {
                println(world)
                println()
            }
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

    fun sense(direction: Direction, what: Something) { // Skip instruction if cell in direction is not what
        val senseCoord = changeCoord(direction, coord)
        if (world[senseCoord] != what) ip++
        consumeFoodAndIncrementIp()
    }

    fun goto(steps: Int) {
        ip += steps
        ip %= program.size // Go round
        consumeFoodAndIncrementIp()
    }

    private fun changeCoord(direction: Direction, coord: Coord): Coord {
        return with(coord) {
            when (direction) {
                Direction.NORTH -> Coord(x, y - 1)
                Direction.EAST -> Coord(x + 1, y)
                Direction.SOUTH -> Coord(x, y + 1)
                Direction.WEST -> Coord(x - 1, y)
            }
        }
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
