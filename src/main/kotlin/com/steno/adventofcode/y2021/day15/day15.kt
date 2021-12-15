package com.steno.adventofcode.y2021.day15

import com.steno.adventofcode.util.Vector
import com.steno.assignment

data class RiskMap(val values: List<List<Int>>, val repeatsX: Int = 1, val repeatsY: Int = 1) {
    private val unitWidth = values.first().size
    private val unitHeight = values.size
    val width = unitWidth * repeatsX
    val height = unitHeight * repeatsY
    val rangeX = 0 until width
    val rangeY = 0 until height

    val lowestRiskFrom: (Vector) -> Int by lazy {
        { point: Vector ->
            when {
                point.right in this && point.down in this -> minOf(
                    this[point.right] + lowestRiskFrom(point.right),
                    this[point.down] + lowestRiskFrom(point.down),
                )
                point.right in this -> this[point.right] + lowestRiskFrom(point.right)
                point.down in this -> this[point.down] + lowestRiskFrom(point.down)
                else -> 0
            }
        }.memoize()
    }

    fun lowestRisk(): Int {
        val tentativeRisks = mutableMapOf(Vector.ZERO to 0)
        val unvisited = rangeX.asSequence().flatMap { x -> rangeY.asSequence().map { y -> Vector(x, y) } }.toMutableSet()
        sequence {
            yield(Vector.ZERO)
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

        return tentativeRisks[Vector(width - 1, height - 1)]!!
    }

    fun Vector.neighbours() = sequenceOf(right, down, left, up).filter { it in this@RiskMap }
    val Vector.right get() = this + Vector.UNIT_X
    val Vector.down get() = this + Vector.UNIT_Y
    val Vector.left get() = this - Vector.UNIT_X
    val Vector.up get() = this - Vector.UNIT_Y

    operator fun get(point: Vector) = point
        .let { (x, y) -> values[y % unitHeight][x % unitHeight] + y / unitHeight + x / unitWidth }
        .let { (it - 1) % 9 + 1 }

    operator fun contains(point: Vector) = point.let { (x, y) -> x in rangeX && y in rangeY }
}

private fun main() {
    assignment("2021/day15") { parse(it) }
        .eval { it.lowestRiskFrom(Vector.ZERO) }
        .eval { it.copy(repeatsX = 5, repeatsY = 5).lowestRiskFrom(Vector.ZERO) }
        .eval { it.lowestRisk() }
        .eval { it.copy(repeatsX = 5, repeatsY = 5).lowestRisk() }
}

fun parse(lines: Sequence<String>) = RiskMap(lines.map { line -> line.map { it.digitToInt() } }.toList())

fun <T, R> ((T) -> R).memoize(): (T) -> R {
    val cache = mutableMapOf<T, R>()
    return {
        cache.getOrPut(it) { this(it) }
    }
}
