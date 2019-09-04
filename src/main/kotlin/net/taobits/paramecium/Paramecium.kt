package net.taobits.paramecium

const val INITAL_ENERGY = 10
const val CONSUMPTION_PER_COMMAND = 1
const val ENERGY_PER_FOOD = 3

data class Paramecium(var coord: Coord = Coord(0, 0), var program: Program = emptyList()) {
    lateinit var world: World
    var energy: Int = INITAL_ENERGY

    fun live(debug: Boolean = false) {
        energy = INITAL_ENERGY
        ProgrammProcessor(program).execute(this, debug)
    }

    fun move(direction: Direction) {
        val nextCoord = changeCoord(direction, coord)
        val movePossible = world[nextCoord] != Something.WALL
        if (movePossible) {
            coord = nextCoord
            eat()
        } else consumeEnergy() // Extra penality when hitting the wall
    }

    fun sense(direction: Direction, what: Something): Boolean { // Skip instruction if cell in direction is not what
        val senseCoord = changeCoord(direction, coord)
        return world[senseCoord] == what
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
            energy += ENERGY_PER_FOOD
        }
    }

    fun consumeEnergy() {
        energy -= CONSUMPTION_PER_COMMAND
    }

    override fun toString() = """
        energy: $energy
        pos: $coord
        program: $program
    """.trimIndent()
}

data class Coord(val x: Int, val y: Int)
