package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.Grid
import com.steno.adventofcode.util.math.Vector2

class Day12 : AdventOfCodeSpec({ challenge ->
    challenge.map { HeightMap.parse(it) }
        .eval(31, 468) { it.dijkstraShortestPath() }
        .eval(29, 459) { it.dijkstraShortestPath2() }
}) {
    data class HeightMap(
        val grid: Grid<Char>,
        val start: Vector2,
        val end: Vector2,
    ) {
        fun dijkstraShortestPath(): Int {
            val tentativeDistances = mutableMapOf<Vector2, Int>().also { it[start] = 0 }
            val previousNode = mutableMapOf<Vector2, Vector2>()
            val unvisited = grid.indices.toMutableSet()
            var current: Vector2
            do {
                current = unvisited.minBy { tentativeDistances[it] ?: Int.MAX_VALUE }
                unvisited -= current
                grid.neighboursOf(current)
                    .filter { it in unvisited }
                    .filter { grid[it] - grid[current] <= 1 }
                    .forEach { neighbour ->
                        val alternativeDistance = tentativeDistances[current]!! + 1
                        if (alternativeDistance < (tentativeDistances[neighbour] ?: Int.MAX_VALUE)) {
                            tentativeDistances[neighbour] = alternativeDistance
                            previousNode[neighbour] = current
                        }
                    }
            } while (end in unvisited)
            val path = generateSequence(end) {
                previousNode[it]!!
            }.takeWhile { it != start }
            return path.count()
        }

        fun dijkstraShortestPath2(): Int {
            val tentativeDistances = mutableMapOf<Vector2, Int>().also { it[start] = 0 }
            val previousNode = mutableMapOf<Vector2, Vector2>()
            val unvisited = grid.indices.toMutableSet()
            var current: Grid<Char>.Navigator
            do {
                current = grid.navigate(unvisited.minBy { tentativeDistances[it] ?: Int.MAX_VALUE })
                unvisited -= current.point
                current.neighbours
                    .filter { it.point in unvisited }
                    .filter { it.value - current.value <= 1 }
                    .forEach { n ->
                        val alternativeDistance = if (current.value == 'a') 0 else tentativeDistances[current.point]!! + 1
                        if (alternativeDistance < (tentativeDistances[n.point] ?: Int.MAX_VALUE)) {
                            tentativeDistances[n.point] = alternativeDistance
                            previousNode[n.point] = current.point
                        }
                    }
            } while (end in unvisited)
            val path = generateSequence(end) {
                previousNode[it]!!
            }.takeWhile { grid[it] != 'a' }
            return path.count()
        }

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
