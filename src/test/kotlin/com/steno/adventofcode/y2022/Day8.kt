package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.Grid
import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_X
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_Y
import com.steno.adventofcode.util.takeUntil
import com.steno.adventofcode.y2022.Day8.HeightMap

class Day8 : AdventOfCodeSpec({ challenge ->
    challenge.map { asHeightMap(it) }
        .eval(21, 1798) {
            it.visible.count()
        }
        .eval(8, 259308) { heightMap ->
            heightMap.points.maxOf { heightMap.scenicScoreAt(it) }
        }
}) {
    data class HeightMap(val grid: Grid<Int>) {
        val points = grid.indices

        fun viewingDistanceAt(at: Vector2, dir: Vector2) = grid.indicesFrom(at + dir, dir)
            .takeUntil { grid[it] >= grid[at] }
            .count()

        fun scenicScoreAt(at: Vector2) = sequenceOf(UNIT_X, -UNIT_X, UNIT_Y, -UNIT_Y)
            .map { viewingDistanceAt(at, it) }
            .reduce(Int::times)

        fun visibleTreesAt(at: Vector2, dir: Vector2): Sequence<Vector2> {
            var max = -1
            return sequence {
                for (point in grid.indicesFrom(at, dir)) {
                    val h = grid[point]
                    if (h > max) {
                        yield(point)
                        max = h
                    }
                }
            }
        }

        val visibleLeft = grid.columns.first.indices.flatMap { p -> visibleTreesAt(p, UNIT_X) }
        val visibleRight = grid.columns.last.indices.flatMap { p -> visibleTreesAt(p, -UNIT_X) }
        val visibleTop = grid.rows.first.indices.flatMap { p -> visibleTreesAt(p, UNIT_Y) }
        val visibleBottom = grid.rows.last.indices.flatMap { p -> visibleTreesAt(p, -UNIT_Y) }

        val visible = (visibleLeft + visibleTop + visibleRight + visibleBottom).toSet()
    }
}

private fun asHeightMap(lines: Sequence<String>) = lines
    .map { line -> line.map { it.digitToInt() } }
    .toList()
    .let { HeightMap(Grid(it)) }
