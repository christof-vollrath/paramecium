package net.taobits.paramecium

import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class WorldSpek: Spek({
    describe("a world with paramecia int it") {
        given("a textual representation of the world") {
            val ascii = """
                xxxxxxxxx
                xo......x
                x...x...x
                x..xxx..x
                x...x...x
                x.......x
                xxxxxxxxx
            """.trimIndent()

            it("should be parsed and printed correctly") {
                World(ascii).toString() `should equal` ascii
            }
        }
    }
})
