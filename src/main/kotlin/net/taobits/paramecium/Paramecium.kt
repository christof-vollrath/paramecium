package net.taobits.paramecium

typealias Program = List<Move>

class Paramecium(var coord: Coord = Coord(0, 0), val program: Program = emptyList()) {
    fun start() {
        program.forEach { it.execute(this) }
    }

    fun move(direction: Direction) {
        with (coord) {
            when(direction) {
                Direction.NORTH -> coord = Coord(x, y - 1)
                Direction.EAST -> coord = Coord(x + 1, y)
                Direction.SOUTH -> coord = Coord(x, y + 1)
                Direction.WEST -> coord = Coord(x- 1, y)
            }
        }
    }

}

data class Coord(val x: Int, val y: Int)

class Move(val direction: Direction) {
    fun execute(paramecium: Paramecium) = paramecium.move(direction)
}

enum class Direction { NORTH, EAST, SOUTH, WEST  }
