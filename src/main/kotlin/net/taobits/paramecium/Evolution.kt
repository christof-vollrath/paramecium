package net.taobits.paramecium

typealias CreateIndividual<T>  = () -> T
typealias Fitness<T> = (T) -> Double
typealias Mutate<T> = (T) -> T

data class EvolutionConfiguration<T>(
        val populationSize: Int = 100,
        val createIndividual: CreateIndividual<T>,
        val mutate: Mutate<T>,
        val fitness: Fitness<T>,
        val generations: Int = 100
)
data class EvolutionResult<T>(val best: T, val fitness: Double)

fun <T>evolve(evolutionConfiguration: EvolutionConfiguration<T>): EvolutionResult<T> {
    with (evolutionConfiguration) {
        var population = (1..evolutionConfiguration.populationSize).map {
            createIndividual()
        }
        var generation = 1
        while (true) {
            val result = population.map { paramecium ->
                paramecium to fitness(paramecium)
            }
            val bestResult = result.maxBy { it.second }!!
            if (generation == generations)
                return EvolutionResult(bestResult.first, bestResult.second)
            population = List(populationSize) { bestResult.first }.map { mutate(it) }
            generation++
        }
    }
}
