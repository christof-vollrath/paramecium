package net.taobits.paramecium

import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.util.*

class EvolutionSpek: Spek({
    val randomGenerator = Random(1234L)

    describe("fill a list") {
        given("a small list") {
            val smallList = listOf(1, 2, 3)
            val result = filledListOf(8, smallList)
            it ("should create a new filled list with size 8") {
                result.size `should equal` 8
            }
            it ("should create a new filled list") {
                result `should equal` listOf(1, 2, 3, 1, 2, 3, 1, 2)
            }
        }
    }
    val asciiWorld = """
                xxxxxxxxx
                xo......x
                x...x...x
                x..xxx..x
                x...x...x
                x.......x
                xxxxxxxxx
            """.trimIndent()
    var fitness0: Double? = null
    describe("Strategy 0 - only one generation") {
        it("should find an efficent paramecium just by creating random paramecia without generations and mutation") {
            val programSize = 20
            val createIndividual = {
                val program = (1..programSize).map {
                    RandomCommandGenerator.createRandomCommand(programSize)
                }
                Paramecium(program = program)
            }
            val fitness = { paramecium: Paramecium ->
                val world = World(asciiWorld, paramecium)
                world.start()
                paramecium.programmProcessor.ticks.toDouble()
            }
            val result = evolve(EvolutionConfiguration(createIndividual = createIndividual, fitness = fitness, mutate = {it}, generations = 1))
            result.fitness `should be greater than` 0.0
            val world = World(asciiWorld, result.best)
            world.start(debug = true)
            fitness0 = result.fitness
            println(result.best)
        }
    }
    describe("Strategy 1") {
        it("should find an efficent paramecium better than with strategy 0") {
            val programSize = 20
            val createIndividual = {
                val program = (1..programSize).map {
                    RandomCommandGenerator.createRandomCommand(programSize)
                }
                Paramecium(program = program)
            }
            val fitness = { paramecium: Paramecium ->
                val world = World(asciiWorld, paramecium)
                world.start()
                paramecium.programmProcessor.ticks.toDouble()
            }
            val mutate = { paramecium: Paramecium ->
                val mutatedProgram = paramecium.program.map {command ->
                    val rand = randomGenerator.nextDouble()
                    if (rand < 0.1) {
                        RandomCommandGenerator.createRandomCommand(paramecium.program.size)
                    }
                    else command
                }
                paramecium.copy(program = mutatedProgram)
            }
            val evolutionConfiguration = EvolutionConfiguration(
                    populationSize = 2000,
                    generations = 1000,
                    createIndividual = createIndividual,
                    fitness = fitness,
                    mutate = mutate)
            val result = evolve(evolutionConfiguration)
            result.fitness `should be greater than` 0.0
            val world = World(asciiWorld, result.best)
            world.start(debug = true)
            if (fitness0 != null) result.fitness `should be greater than` fitness0!! // compare to earlier results
            println(result.best)
        }
    }
    describe("Strategy 1a") {
        val asciiWorlds = listOf(
            """
                xxxxxxxxx
                xo......x
                x...x...x
                x..xxx..x
                x...x...x
                x.......x
                xxxxxxxxx
            """.trimIndent(),
                """
                xxxxxxxxx
                x......ox
                x...x...x
                x..xxx..x
                x...x...x
                x.......x
                xxxxxxxxx
            """.trimIndent(),
                """
                xxxxxxxxx
                x.......x
                x...x...x
                x..xxx..x
                x...x...x
                x......ox
                xxxxxxxxx
            """.trimIndent(),
                """
                xxxxxxxxx
                x.......x
                x...x...x
                x..xxx..x
                x...x...x
                xo......x
                xxxxxxxxx
            """.trimIndent(),
                """
                xxxxxxxxx
                x.......x
                x..ox...x
                x..xxx..x
                x...x...x
                x.......x
                xxxxxxxxx
            """.trimIndent(),
                """
                xxxxxxxxx
                x.......x
                x...xo..x
                x..xxx..x
                x...x...x
                x.......x
                xxxxxxxxx
            """.trimIndent(),
                """
                xxxxxxxxx
                x.......x
                x...x...x
                x..xxx..x
                x...xo..x
                x.......x
                xxxxxxxxx
            """.trimIndent(),
                """
                xxxxxxxxx
                x.......x
                x...x...x
                x..xxx..x
                x..ox...x
                x.......x
                xxxxxxxxx
            """.trimIndent()
        )

        it("should find an efficent paramecium which does not rely on starting position") {
            val programSize = 20
            val createIndividual = {
                val program = (1..programSize).map {
                    RandomCommandGenerator.createRandomCommand(programSize)
                }
                Paramecium(program = program)
            }
            val fitness = { paramecium: Paramecium ->
                asciiWorlds.map {ascWorld ->
                    val world = World(ascWorld, paramecium)
                    world.start()
                    paramecium.programmProcessor.ticks
                }.sum().toDouble()
            }
            val mutate = { paramecium: Paramecium ->
                val mutatedProgram = paramecium.program.map {command ->
                    val rand = randomGenerator.nextDouble()
                    if (rand < 0.1) {
                        RandomCommandGenerator.createRandomCommand(paramecium.program.size)
                    }
                    else command
                }
                paramecium.copy(program = mutatedProgram)
            }
            val evolutionConfiguration = EvolutionConfiguration(
                    populationSize = 100_000,
                    nrParents = 1000,
                    generations = 100,
                    createIndividual = createIndividual,
                    fitness = fitness,
                    mutate = mutate)
            val result = evolve(evolutionConfiguration, debug = true)
            result.fitness `should be greater than` 0.0
            val world = World(asciiWorlds[0], result.best)
            world.start(debug = true)
            println(result.best)
        }
    }
})
