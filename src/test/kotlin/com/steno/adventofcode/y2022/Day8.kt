package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.Vector2
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

        fun viewingDistanceAt(at: Vector2, dir: Vector2) = generateSequence(at + dir) { it + dir }
            .takeUntil { it !in this || this[it] >= this[at] }
            .filter { it in this }
            .count()

        fun scenicScoreAt(at: Vector2) = sequenceOf(Vector2.UNIT_X, Vector2.UNIT_Y, -Vector2.UNIT_X, -Vector2.UNIT_Y)
            .map { viewingDistanceAt(at, it) }
            .reduce(Int::times)

        val visibleLeft = rangeY.flatMap { y ->
            sequence {
                var max = -1
                for (x in rangeX) {
                    val h = this@HeightMap[x to y]
                    if (h > max) {
                        yield(x to y)
                        max = h
                    }
                }
            }
        }

        val visibleRight = rangeY.flatMap { y ->
            sequence {
                var max = -1
                for (x in rangeX.reversed()) {
                    val h = this@HeightMap[x to y]
                    if (h > max) {
                        yield(x to y)
                        max = h
                    }
                }
            }
        }

        val visibleTop = rangeX.flatMap { x ->
            sequence {
                var max = -1
                for (y in rangeY) {
                    val h = this@HeightMap[x to y]
                    if (h > max) {
                        yield(x to y)
                        max = h
                    }
                }
            }
        }

        val visibleBottom = rangeX.flatMap { x ->
            sequence {
                var max = -1
                for (y in rangeY.reversed()) {
                    val h = this@HeightMap[x to y]
                    if (h > max) {
                        yield(x to y)
                        max = h
                    }
                }
            }
        }

        val visible = (visibleLeft + visibleTop + visibleRight + visibleBottom)
            .toSet()

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
