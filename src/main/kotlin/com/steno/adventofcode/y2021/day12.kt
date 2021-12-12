package com.steno.adventofcode.y2021

import com.steno.assignment

private const val START = "start"
private const val END = "end"

private data class Nodes(val edgesById: Map<String, Set<String>> = emptyMap()) {
    fun paths(
        start: String = START, end: String = END,
        visitedCaves: Set<String> = setOf(start),
        allowSecondSmallCaveVisit: Boolean = false
    ): Sequence<Sequence<String>> = when (start) {
        end -> sequenceOf(sequenceOf(end))
        !in edgesById -> emptySequence()
        else -> {
            start.neighbours
                .filter { it != START }
                .filter { !it.smallCave || it !in visitedCaves || allowSecondSmallCaveVisit }
                .flatMap {
                    paths(
                        it, end,
                        visitedCaves = visitedCaves + start,
                        allowSecondSmallCaveVisit = allowSecondSmallCaveVisit && (!it.smallCave || it !in visitedCaves)
                    ).map { nextPath -> sequenceOf(start) + nextPath }
                }
        }
    }

    private val String.neighbours get() = edgesById[this]!!.asSequence()
    private val String.smallCave get() = lowercase() == this
}

private fun main() {
    assignment("2021/day12") { parseCaves(it) }
        .eval { it.paths(allowSecondSmallCaveVisit = false).count() }
        .eval { it.paths(allowSecondSmallCaveVisit = true).count() }
}

private fun parseCaves(lines: Sequence<String>) = lines
    .map {
        it.split("-").let { (cave1, cave2) ->
            mapOf(cave1 to setOf(cave2), cave2 to setOf(cave1))
        }
    }
    .flatMap { it.asSequence() }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, allEdges) -> allEdges.flatten().toSet() }
    .let { Nodes(it) }
