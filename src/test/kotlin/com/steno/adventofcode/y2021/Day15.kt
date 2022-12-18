package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.math.Vector2

private class Day15: AdventOfCodeSpec({ challenge ->
    challenge.map { parse(it) }
        .eval(40, 363) { it.lowestRisk() }
        .eval(315, 2835) { it.copy(repeatsX = 5, repeatsY = 5).lowestRisk() }
}) {
    data class RiskMap(val values: List<List<Int>>, val repeatsX: Int = 1, val repeatsY: Int = 1) {
        private val unitWidth = values.first().size
        private val unitHeight = values.size
        val width = unitWidth * repeatsX
        val height = unitHeight * repeatsY
        val rangeX = 0 until width
        val rangeY = 0 until height

        fun lowestRisk(): Int {
            val tentativeRisks = mutableMapOf(Vector2.ZERO to 0)
            val unvisited = rangeX.asSequence().flatMap { x -> rangeY.asSequence().map { y -> Vector2(x, y) } }.toMutableSet()
            sequence {
                yield(Vector2.ZERO)
                while (unvisited.isNotEmpty()) {
                    unvisited
                        .minByOrNull { tentativeRisks[it] ?: Int.MAX_VALUE }
                        ?.let { yield(it.also(unvisited::remove)) }
                }
            }.forEach { p ->
                p.neighbours()
                    .filter { it in unvisited }
                    .forEach { n ->
                        tentativeRisks[n] = (tentativeRisks[p]!! + this[n]).let { newRisk ->
                            tentativeRisks[n]?.let { minOf(it, newRisk) } ?: newRisk
                        }
                    }
            }

            return tentativeRisks[Vector2(width - 1, height - 1)]!!
        }

        fun Vector2.neighbours() = sequenceOf(right, down, left, up).filter { it in this@RiskMap }
        val Vector2.right get() = this + Vector2.UNIT_X
        val Vector2.down get() = this + Vector2.UNIT_Y
        val Vector2.left get() = this - Vector2.UNIT_X
        val Vector2.up get() = this - Vector2.UNIT_Y

        operator fun get(point: Vector2) = point
            .let { (x, y) -> values[y % unitHeight][x % unitHeight] + y / unitHeight + x / unitWidth }
            .let { (it - 1) % 9 + 1 }

        operator fun contains(point: Vector2) = point.let { (x, y) -> x in rangeX && y in rangeY }
    }

    companion object {
        fun parse(lines: Sequence<String>) = RiskMap(lines.map { line -> line.map { it.digitToInt() } }.toList())
    }
}
