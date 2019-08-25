package net.taobits.paramecium

import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

class ParameciumSpek: Spek({
    describe("a paramecium and what it can do") {
        given("one nice paramecium at 1, 1") {
            val paramecium = Paramecium(Coord(1, 1))

            it("should be at coord 1, 1") {
                paramecium.coord `should equal` Coord(1, 1)
            }
            given("movement commands") {
                val testData = arrayOf(
                        //  | direction            | result
                        //--|----------------------|------------------------
                        data( Move(Direction.NORTH), Coord(2, 1)),
                        data( Move(Direction.EAST), Coord(3, 2)),
                        data( Move(Direction.SOUTH), Coord(2, 3)),
                        data( Move(Direction.WEST), Coord(1, 2))
                )
                onData("move paramecium by command %s", with = *testData) { command, expectedCoord ->
                    val paramecium = Paramecium(Coord(2, 2))
                    val movedCoord = command.execute(paramecium)
                    it("should be moved to $expectedCoord") {
                        paramecium.coord `should equal` expectedCoord
                    }
                }
            }
            given("a mparamecium with a program") {
                val paramecium = Paramecium(Coord(2, 2), listOf(Move(Direction.NORTH), Move(Direction.EAST)))
                it("should execute the program when started") {
                    paramecium.start()
                    paramecium.coord `should equal` Coord(3, 1)
                }
            }
        }
    }
})


