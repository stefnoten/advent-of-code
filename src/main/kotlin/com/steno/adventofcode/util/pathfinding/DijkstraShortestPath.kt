package com.steno.adventofcode.util.pathfinding

fun <N> dijkstraShortestPathFrom(
    start: N,
    destination: N,
    nodes: Sequence<N>,
    edgesOf: (N) -> Sequence<Edge<N>>,
) = dijkstraShortestPathFromAny(sequenceOf(start), destination, nodes, edgesOf)

fun <N> dijkstraShortestPathFromAny(
    start: Sequence<N>,
    destination: N,
    nodes: Sequence<N>,
    edgesOf: (N) -> Sequence<Edge<N>>,
) = dijkstraShortestPath(start, nodes, edgesOf) { destination in it }

fun <N> dijkstraShortestPathFrom(
    start: N,
    nodes: Sequence<N>,
    edgesOf: (N) -> Sequence<Edge<N>>,
) = dijkstraShortestPathFromAny(sequenceOf(start), nodes, edgesOf)

fun <N> dijkstraShortestPathFromAny(
    start: Sequence<N>,
    nodes: Sequence<N>,
    edgesOf: (N) -> Sequence<Edge<N>>,
) = dijkstraShortestPath(start, nodes, edgesOf) { it.isNotEmpty() }

private fun <N> dijkstraShortestPath(
    start: Sequence<N>,
    nodes: Sequence<N>,
    edgesOf: (N) -> Sequence<Edge<N>>,
    shouldContinue: (unvisited: Set<N>) -> Boolean,
): DijkstraShortestPath<N> {
    val tentativeDistances = mutableMapOf<N, Int>().also { distances ->
        start.forEach { distances[it] = 0 }
    }
    val previousNode = mutableMapOf<N, N>()
    val unvisited = nodes.toMutableSet()
    var current: N
    do {
        current = unvisited.minBy { tentativeDistances[it] ?: Int.MAX_VALUE }
        unvisited -= current

        val distanceToCurrent = tentativeDistances[current] ?: Int.MAX_VALUE
        if (distanceToCurrent == Int.MAX_VALUE) {
            continue
        }
        edgesOf(current)
            .filter { it.to in unvisited }
            .forEach { edge ->
                val alternativeDistance = distanceToCurrent + edge.distance
                if (alternativeDistance < (tentativeDistances[edge.to] ?: Int.MAX_VALUE)) {
                    tentativeDistances[edge.to] = alternativeDistance
                    previousNode[edge.to] = current
                }
            }
    } while (shouldContinue(unvisited))
    return DijkstraShortestPath(tentativeDistances, previousNode)
}

data class Edge<N>(val to: N, val distance: Int)

class DijkstraShortestPath<N>(
    val distances: Map<N, Int>,
    private val previousNode: Map<N, N>,
) {
    fun pathTo(destination: N): List<N> = generateSequence(destination) { previousNode[it] }.toList().reversed()

    fun stepsTo(destination: N): Int = pathTo(destination).size - 1

    fun distanceTo(destination: N): Int = distances[destination] ?: Int.MAX_VALUE

    fun canReach(destination: N): Boolean = distanceTo(destination) != Int.MAX_VALUE
}
