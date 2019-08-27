package net.taobits.paramecium

import org.amshove.kluent.`should be in range`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.sequences.sequence

class CommandsSpek: Spek({
    describe("commands should be created randomly") {
        on("create random commands") {
            val randomCommands = sequence {
                repeat(1000) {
                    yield(RandomCommandGenerator.createRandomCommand(10))
                }
            }.toList()
            it("should have created all commands with a similar frequency") {
                val groupedCommands = randomCommands.groupBy { it::class }
                groupedCommands.size `should equal` 3 // every command should be created
                groupedCommands.values.forEach {
                    it.size `should be in range` 100..400
                }
            }
            it("should have created move commands in every direction") {
                val moveCommands = randomCommands.filterIsInstance<Move>()
                val groupedMoves = moveCommands.groupBy { it.direction }
                groupedMoves.size `should equal` 4
            }
            it("should have created sense commands in every direction") {
                val moveCommands = randomCommands.filterIsInstance<Sense>()
                val groupedMoves = moveCommands.groupBy { it.direction }
                groupedMoves.size `should equal` 4
            }
            it("should have created sense commands for everything") {
                val moveCommands = randomCommands.filterIsInstance<Sense>()
                val groupedMoves = moveCommands.groupBy { it.what }
                groupedMoves.size `should equal` 3
            }
            it("should have created goto commands in range") {
                val moveCommands = randomCommands.filterIsInstance<Goto>()
                val groupedMoves = moveCommands.groupBy { it.steps }
                groupedMoves.size `should equal` 10 // 1..10
            }
        }
    }
})
