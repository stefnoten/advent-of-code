package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.untilStable

private class Day09 : AdventOfCodeSpec({ challenge ->
    challenge.map { lines -> HeightMap(lines.map { line -> line.map { it.digitToInt() } }.toList()) }
        .eval(15, 594) { heightMap -> heightMap.lowpoints.sumOf { it.riskLevel } }
        .eval(1134, 858494) { heightMap ->
            heightMap.lowpoints
                .map { heightMap.basinAt(it).size }
                .sortedDescending()
                .take(3)
                .reduce(Int::times)
        }
}) {
    data class LowPoint(val point: Pair<Int, Int>, val height: Int) {
        val riskLevel = height + 1
    }

    data class HeightMap(val values: List<List<Int>>) {
        val width = values.first().size
        val height = values.size
        val rangeX = 0 until width
        val rangeY = 0 until height
        val lowpoints
            get() = rangeX.flatMap { x -> rangeY.map { y -> x to y } }
                .filter { isLowpoint(it) }
                .map { LowPoint(it, this[it]) }

        fun isLowpoint(point: Pair<Int, Int>) = this[point].let { height ->
            neighbours(point).map { this[it] }.all { it > height }
        }

        fun neighbours(point: Pair<Int, Int>) = point.let { (x, y) ->
            sequenceOf(-1 to 0, 0 to -1, 1 to 0, 0 to 1)
                .map { (dx, dy) -> x + dx to y + dy }
                .filter { it in this }
        }

        fun basinAt(lowPoint: LowPoint) = generateSequence(setOf(lowPoint.point)) {
            it + it.flatMap { p -> neighbours(p).filter { n -> this[n] in (this[p] + 1) until 9 } }.toSet()
        }.untilStable { it.size }.last()

        operator fun get(point: Pair<Int, Int>) = point.let { (x, y) -> values[y][x] }

        operator fun contains(point: Pair<Int, Int>) = point.let { (x, y) -> x in rangeX && y in rangeY }
    }
}
