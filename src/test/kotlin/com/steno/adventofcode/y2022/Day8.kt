package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
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
    data class HeightMap(val values: List<List<Int>>) {
        val width = values.first().size
        val height = values.size
        val rangeX = 0 until width
        val rangeY = 0 until height
        val points = rangeY.flatMap { y -> rangeX.map { x -> Vector2(x, y) } }

        fun viewingDistanceAt(at: Vector2, dir: Vector2) = pointsFrom(at + dir, dir)
            .takeUntil { this[it] >= this[at] }
            .count()

        fun scenicScoreAt(at: Vector2) = sequenceOf(UNIT_X, -UNIT_X, UNIT_Y, -UNIT_Y)
            .map { viewingDistanceAt(at, it) }
            .reduce(Int::times)

        private fun pointsFrom(from: Vector2, dir: Vector2) = generateSequence(from) { it + dir }.takeWhile { it in this }

        fun visibleTreesAt(at: Vector2, dir: Vector2): Sequence<Vector2> {
            var max = -1
            return sequence {
                for (point in pointsFrom(at, dir)) {
                    val h = this@HeightMap[point]
                    if (h > max) {
                        yield(point)
                        max = h
                    }
                }
            }
        }

        val visibleLeft = rangeY.flatMap { y -> visibleTreesAt(Vector2(rangeX.first, y), UNIT_X) }
        val visibleRight = rangeY.flatMap { y -> visibleTreesAt(Vector2(rangeX.last, y), -UNIT_X) }
        val visibleTop = rangeX.flatMap { x -> visibleTreesAt(Vector2(x, rangeY.first), UNIT_Y) }
        val visibleBottom = rangeX.flatMap { x -> visibleTreesAt(Vector2(x, rangeY.last), -UNIT_Y) }

        val visible = (visibleLeft + visibleTop + visibleRight + visibleBottom).toSet()

        operator fun get(point: Pair<Int, Int>) = point.let { (x, y) -> values[y][x] }

        operator fun get(point: Vector2) = point.let { (x, y) -> values[y][x] }

        operator fun contains(point: Pair<Int, Int>) = point.let { (x, y) -> x in rangeX && y in rangeY }

        operator fun contains(point: Vector2) = point.let { (x, y) -> x in rangeX && y in rangeY }
    }
}

private fun asHeightMap(lines: Sequence<String>) = lines
    .map { line -> line.map { it.digitToInt() } }
    .toList()
    .let { HeightMap(it) }
