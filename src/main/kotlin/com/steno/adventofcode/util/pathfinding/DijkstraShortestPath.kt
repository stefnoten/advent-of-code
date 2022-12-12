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
        edgesOf(current)
            .filter { it.to in unvisited }
            .forEach { edge ->
                val alternativeDistance = tentativeDistances[current]!! + edge.distance
                if (alternativeDistance < (tentativeDistances[edge.to] ?: Int.MAX_VALUE)) {
                    tentativeDistances[edge.to] = alternativeDistance
                    previousNode[edge.to] = current
                }
            }
    } while (destination in unvisited)
    return DijkstraShortestPath(destination, tentativeDistances, previousNode)
}

data class Edge<N>(val to: N, val distance: Int)

class DijkstraShortestPath<N>(
    val destination: N,
    val distances: Map<N, Int>,
    private val previousNode: Map<N, N>,
) {
    val path: List<N> by lazy {
        generateSequence(destination) { previousNode[it] }.toList().reversed()
    }
    val steps: Int
        get() = path.size - 1
}
