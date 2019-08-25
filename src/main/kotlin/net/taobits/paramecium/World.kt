package net.taobits.paramecium

class World(ascii: String) {
    var environment: Array<Array<Something>>
    val paramecium: Paramecium

    init {
        val (parsedEnvironment, parameciumCoord)  = parse(ascii)
        environment = parsedEnvironment
        paramecium = Paramecium(parameciumCoord)
    }

    fun parse(ascii: String): Pair<Array<Array<Something>>, Coord> {
        var foundCoord: Coord? = null
        environment = ascii.split("\n").mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                when(c) {
                    'x' -> Something.WALL
                    '.' -> Something.FOOD
                    ' ' -> Something.EMPTY
                    'o' -> {
                        foundCoord = Coord(x, y)
                        Something.EMPTY
                    }
                    else -> throw IllegalArgumentException("Unexpected char $c in world description")
                }
            }.toTypedArray()
        }.toTypedArray()
        val parameciumCoord = foundCoord
        if (parameciumCoord != null) return Pair(environment, parameciumCoord)
        else throw IllegalArgumentException("World description must contain o for position of paramecium")
    }

    override fun toString(): String = environment.mapIndexed { y, line ->
        line.mapIndexed { x, cell ->
            with (paramecium) {
                if (coord.x == x && coord.y == y) 'o'
                else
                    when(cell) {
                        Something.WALL -> 'x'
                        Something.FOOD -> '.'
                        Something.EMPTY -> ' '
                    }
            }
        }.joinToString("")
    }.joinToString("\n")
}

enum class Something { WALL, FOOD, EMPTY }