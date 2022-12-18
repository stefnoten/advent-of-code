package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.Grid
import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.pathfinding.Edge
import com.steno.adventofcode.util.pathfinding.dijkstraShortestPathFrom
import com.steno.adventofcode.util.pathfinding.dijkstraShortestPathFromAny

class Day12 : AdventOfCodeSpec({ challenge ->
    challenge.map { HeightMap.parse(it) }
        .eval(31, 468) { it.shortestPathFromStart() }
        .eval(29, 459) { it.shortestPathFromAnyA() }
}) {
    data class HeightMap(
        val grid: Grid<Char>,
        val start: Vector2,
        val end: Vector2,
    ) {
        private val indicesWithA get() = grid.indices.filter { grid[it] == 'a' }

        fun shortestPathFromStart(): Int = dijkstraShortestPathFrom(start, end, grid.indices) {
            edgesFrom(it)
        }.stepsTo(end)
        fun shortestPathFromAnyA(): Int = dijkstraShortestPathFromAny(indicesWithA, end, grid.indices) {
            edgesFrom(it)
        }.stepsTo(end)

        private fun edgesFrom(from: Vector2) = grid.neighboursOf(from).asSequence()
            .filter { grid[it] - grid[from] <= 1 }
            .map { Edge(it, 1) }

        companion object {
            fun parse(lines: Sequence<String>): HeightMap {
                var start: Vector2 = Vector2.ZERO
                var end: Vector2 = Vector2.ZERO
                return lines.mapIndexed { y, line ->
                    line.mapIndexed { x, c ->
                        when (c) {
                            'S' -> 'a'.also { start = Vector2(x, y) }
                            'E' -> 'z'.also { end = Vector2(x, y) }
                            else -> c
                        }
                    }.toList()
                }.toList().let { HeightMap(Grid(it), start, end) }
            }
        }
    }
}
