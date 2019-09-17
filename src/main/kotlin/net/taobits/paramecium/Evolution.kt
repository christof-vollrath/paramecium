package net.taobits.paramecium

typealias CreateIndividual<T>  = () -> T
typealias Fitness<T> = (T) -> Double
typealias Mutate<T> = (T) -> T

data class EvolutionConfiguration<T>(
        val populationSize: Int = 100,
        val nrParents: Int = 1,
        val createIndividual: CreateIndividual<T>,
        val mutate: Mutate<T>,
        val fitness: Fitness<T>,
        val generations: Int = 100
)
data class EvolutionResult<T>(val best: T, val fitness: Double)

fun <T> evolve(evolutionConfiguration: EvolutionConfiguration<T>, debug: Boolean = false): EvolutionResult<T> {
    with (evolutionConfiguration) {
        var population = (1..evolutionConfiguration.populationSize).map {
            createIndividual()
        }
        var generation = 1
        while (true) {
            val result = population.map { paramecium ->
                paramecium to fitness(paramecium)
            }
            val sortedResults = result.sortedByDescending { it.second }
            val bestResult = sortedResults.first()
            if (debug) {
                val worstResult = sortedResults.last()
                println("generation=$generation best=${bestResult.second} worst=${worstResult.second}")
            }
            if (generation == generations)
                return EvolutionResult(bestResult.first, bestResult.second)
            val parents = sortedResults.map{ it.first } .take(nrParents)
            population = filledListOf(populationSize, parents).map { mutate(it) }
            generation++
        }
    }
}

fun <T> filledListOf(size: Int, subList: List<T>) =
        List(size) { i ->
            subList[i % subList.size]
        }
