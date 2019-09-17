package net.taobits.paramecium

import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should be less than`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class EvolutionSpek: Spek({
    val randomGenerator = Random(1234L)

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
})
