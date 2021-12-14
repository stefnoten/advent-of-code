package com.steno.adventofcode.y2021.day7

import com.steno.assignment
import kotlin.math.abs

data class CrabPositions(val positions: List<Int>) {
    private val min = positions.minOrNull()!!
    private val max = positions.maxOrNull()!!
    private val withCount = positions.groupingBy { it }.eachCount()

    fun cheapestTarget(cost: (from: Int, to: Int) -> Int) =
        (min .. max).minOf { to -> withCount.map { (from, count) -> count * cost(from, to) }.sum() }

}

private fun main() {
    assignment("2021/day7") { lines -> CrabPositions(lines.first().split(',').map { it.toInt() }.sorted()) }
        .eval { it.cheapestTarget { from, to -> abs(to - from) } }
        .eval { it.cheapestTarget { from, to -> (abs(to - from) + 1) * abs(to - from) / 2 } }
}
