package net.taobits.paramecium

import org.amshove.kluent.`should be greater than`
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
    describe("Strategy 0") {
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
                val startFood = world.food
                world.start()
                val eaten = startFood - world.food
                eaten.toDouble()
            }
            val result = evolve(EvolutionConfiguration(createIndividual = createIndividual, fitness = fitness, mutate = {it}, generations = 1))
            result.fitness `should be greater than` 0.0
            fitness0 = result.fitness
            val world = World(asciiWorld, result.best)
            val startFood = world.food
            world.start(debug = true)
            println(result.best)
            println("eaten=${startFood - world.food}")
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
                val startFood = world.food
                world.start()
                val eaten = startFood - world.food
                eaten.toDouble()
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
            val result = evolve(EvolutionConfiguration(createIndividual = createIndividual, fitness = fitness, mutate = mutate, generations = 100))
            result.fitness `should be greater than` 0.0
            result.fitness `should be greater than` fitness0!!
            val world = World(asciiWorld, result.best)
            val startFood = world.food
            world.start(debug = true)
            println(result.best)
            println("eaten=${startFood - world.food}")
        }
    }
})

typealias CreateIndividual<T>  = () -> T
typealias Fitness<T> = (T) -> Double
typealias Mutate<T> = (T) -> T

data class EvolutionConfiguration<T>(
        val populationSize: Int = 10_000,
        val createIndividual: CreateIndividual<T>,
        val mutate: Mutate<T>,
        val fitness: Fitness<T>,
        val generations: Int = 1000
)
data class EvolutionResult<T>(val best: T, val fitness: Double)

fun <T>evolve(evolutionConfiguration: EvolutionConfiguration<T>): EvolutionResult<T> {
    with (evolutionConfiguration) {
        var population = (1..evolutionConfiguration.populationSize).map {
            createIndividual()
        }
        var i = 1
        while (true) {
            val result = population.map { paramecium ->
                paramecium to fitness(paramecium)
            }
            val bestResult = result.maxBy { it.second }!!
            if (i == generations)
                return EvolutionResult(bestResult.first, bestResult.second)
            population = List(populationSize) { bestResult.first }.map { mutate(it) }
            i++
        }
    }
}
