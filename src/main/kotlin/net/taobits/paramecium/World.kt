package net.taobits.paramecium

class World(ascii: String, val paramecium: Paramecium = Paramecium()) {
    val food: Int
        get() = environment.map { line ->
            line.filter { it == Something.FOOD }.count()
        }.sum()
    var environment: Array<Array<Something>>

    init {
        val (parsedEnvironment, parameciumCoord)  = parse(ascii)
        environment = parsedEnvironment
        paramecium.world = this
        paramecium.coord = parameciumCoord
    }

    private fun parse(ascii: String): Pair<Array<Array<Something>>, Coord> {
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

    fun start(debug: Boolean = false) = paramecium.live(debug)

    operator fun get(coord: Coord): Something = environment[coord.y][coord.x]
    operator fun set(coord: Coord, value: Something) {
        environment[coord.y][coord.x] = value
    }
}

enum class Something { WALL, FOOD, EMPTY }