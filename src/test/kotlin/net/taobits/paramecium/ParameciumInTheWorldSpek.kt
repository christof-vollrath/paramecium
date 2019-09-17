package net.taobits.paramecium

import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import kotlin.test.expect
import org.jetbrains.spek.data_driven.on as onData

class ParameciumInTheWorldSpek: Spek({
    describe("a paramecium and how it moves") {
        val asciiWorld = """
                xxxxx
                x...x
                x.o.x
                x...x
                xxxxx
            """.trimIndent()
        given("movement commands") {
            val testData = arrayOf(
                    //  | direction            | result
                    //--|----------------------|------------------------
                    data( Move(Direction.NORTH), Coord(2, 1)),
                    data( Move(Direction.EAST),  Coord(3, 2)),
                    data( Move(Direction.SOUTH), Coord(2, 3)),
                    data( Move(Direction.WEST), Coord(1, 2))
            )
            onData("move paramecium by command %s", with = *testData) { command, expectedCoord ->
                val paramecium = Paramecium()
                World(asciiWorld, paramecium) // Paramecium must be part of a world to be able to move
                command.execute(ProgrammProcessor(emptyList()), paramecium)
                it("should be moved to $expectedCoord") {
                    paramecium.coord `should equal` expectedCoord
                }
            }
        }
    }

    describe("a paramecium executing a program") {
        val asciiWorld = """
                xxxxxxxxx
                xo......x
                x...x...x
                x..xxx..x
                x...x...x
                x.......x
                xxxxxxxxx
            """.trimIndent()
        given("a simple world with a paramecium executing a nice program") {
            val program = listOf(Move(Direction.SOUTH), Move(Direction.EAST))
            val paramecium = Paramecium(program = program)
            val world = World(asciiWorld, paramecium)

            on("start the world") {
                it("should move and eat") {
                    world.start()
                    world.toString() `should equal` """
                        xxxxxxxxx
                        x ......x
                        x o.x...x
                        x..xxx..x
                        x...x...x
                        x.......x
                        xxxxxxxxx
                    """.trimIndent()
                }
                it("should have passed 2 ticks") {
                    paramecium.programmProcessor.ticks `should equal` 2
                }
                it("should have increased and consumed food") {
                    paramecium.energy `should equal` INITAL_ENERGY - 2 * CONSUMPTION_PER_COMMAND + 2 * ENERGY_PER_FOOD
                }
            }
        }
        given("a simple world with a paramecium and a program which hits the wall") {
            val program = listOf(Move(Direction.SOUTH), Move(Direction.EAST), Move(Direction.EAST), Move(Direction.EAST))
            val paramecium = Paramecium(program = program)
            val world = World(asciiWorld, paramecium)

            on("start the world") {
                it("should not move into walls") {
                    world.start()
                    world.toString() `should equal` """
                        xxxxxxxxx
                        x ......x
                        x  ox...x
                        x..xxx..x
                        x...x...x
                        x.......x
                        xxxxxxxxx
                    """.trimIndent()
                }
                it("should have passed 4 ticks") {
                    paramecium.programmProcessor.ticks `should equal` 4
                }
                it("should have increased and consumed food but more for hitting the wall") {
                    paramecium.energy `should equal` INITAL_ENERGY - 6 * CONSUMPTION_PER_COMMAND + 3 * ENERGY_PER_FOOD
                }
            }
        }
        given("a simple world with a paramecium and a smart program") {
            val program = listOf(Sense(Direction.NORTH, Something.WALL), Move(Direction.SOUTH))
            val paramecium = Paramecium(program = program)
            val world = World(asciiWorld, paramecium)

            on("start the world") {
                it("should not move into walls") {
                    world.start()
                    world.toString() `should equal` """
                        xxxxxxxxx
                        x ......x
                        xo..x...x
                        x..xxx..x
                        x...x...x
                        x.......x
                        xxxxxxxxx
                    """.trimIndent()
                }
            }
            it("should have passed 2 ticks") {
                paramecium.programmProcessor.ticks `should equal` 2
            }
            it("should have increased and consumed food") {
                paramecium.energy `should equal` INITAL_ENERGY - 2 * CONSUMPTION_PER_COMMAND + 1 * ENERGY_PER_FOOD
            }
        }

        given("a simple world with a paramecium and a another smart program") {
            val program = listOf(Sense(Direction.NORTH, Something.EMPTY), Move(Direction.EAST))
            val paramecium = Paramecium(program = program)
            val world = World(asciiWorld, paramecium)

            on("start the world") {
                it("should not move east because condition is false") {
                    world.start()
                    world.toString() `should equal` """
                        xxxxxxxxx
                        xo......x
                        x...x...x
                        x..xxx..x
                        x...x...x
                        x.......x
                        xxxxxxxxx
                    """.trimIndent()
                }
                it("should have passed 1 tick") {
                    paramecium.programmProcessor.ticks `should equal` 1
                }
                it("should have increased and consumed food") {
                    paramecium.energy `should equal` INITAL_ENERGY - 1 * CONSUMPTION_PER_COMMAND + 0 * ENERGY_PER_FOOD
                }
            }
        }

        given("a simple world with a paramecium and a program with goto") {
            val program = listOf(Move(Direction.SOUTH), Goto(4), Move(Direction.EAST), Move(Direction.EAST), Move(Direction.SOUTH))
            val paramecium = Paramecium(program = program)
            val world = World(asciiWorld, paramecium)

            on("start the world") {
                it("should skip to moves because of the goto") {
                    world.start()
                    world.toString() `should equal` """
                        xxxxxxxxx
                        x ......x
                        x ..x...x
                        xo.xxx..x
                        x...x...x
                        x.......x
                        xxxxxxxxx
                    """.trimIndent()
                }
                it("should have passed 3 ticks") {
                    paramecium.programmProcessor.ticks `should equal` 3
                }
                it("should have increased and consumed food") {
                    paramecium.energy `should equal` INITAL_ENERGY - 3 * CONSUMPTION_PER_COMMAND + 2 * ENERGY_PER_FOOD
                }
            }
        }
        given("a simple world with a paramecium and a program with an endless loop") {
            val program = listOf(Move(Direction.SOUTH), Move(Direction.SOUTH), Move(Direction.NORTH), Move(Direction.NORTH), Goto(0))
            val paramecium = Paramecium(program = program)
            val world = World(asciiWorld, paramecium)

            on("start the world") {
                it("should move until all food is consumed") {
                    world.start()
                    world.toString() `should equal` """
                        xxxxxxxxx
                        x ......x
                        xo..x...x
                        x .xxx..x
                        x...x...x
                        x.......x
                        xxxxxxxxx
                    """.trimIndent()
                }
                it("should have consumed all food") {
                    paramecium.energy `should equal` 0
                }
                it("should have passed 16 ticks") {
                    paramecium.programmProcessor.ticks `should equal` 16
                }
            }
        }
        given("a simple world with a paramecium and a wrong goto") {
            val program = listOf(Move(Direction.SOUTH), Move(Direction.SOUTH), Move(Direction.NORTH), Move(Direction.NORTH), Goto(-2))
            val paramecium = Paramecium(program = program)
            val world = World(asciiWorld, paramecium)

            on("start the world") {
                it("should move until all food is consumed") {
                    world.start()
                    world.toString() `should equal` """
                        xxxxxxxxx
                        xo......x
                        x ..x...x
                        x .xxx..x
                        x...x...x
                        x.......x
                        xxxxxxxxx
                    """.trimIndent()
                }
                it("should have passed 4 ticks") {
                    paramecium.programmProcessor.ticks `should equal` 5
                }
                it("should have increased and consumed food") {
                    paramecium.energy `should equal` INITAL_ENERGY - 5 * CONSUMPTION_PER_COMMAND + 2 * ENERGY_PER_FOOD
                }
            }
        }
    }
})