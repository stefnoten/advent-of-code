package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import kotlin.math.abs

private class Day07: AdventOfCodeSpec({ challenge ->
    challenge.map { lines -> CrabPositions(lines.first().split(',').map { it.toInt() }.sorted()) }
        .eval(37, 342641) { it.cheapestTarget { from, to -> abs(to - from) } }
        .eval(168, 93006301) { it.cheapestTarget { from, to -> (abs(to - from) + 1) * abs(to - from) / 2 } }
}) {
    data class CrabPositions(val positions: List<Int>) {
        private val min = positions.minOrNull()!!
        private val max = positions.maxOrNull()!!
        private val withCount = positions.groupingBy { it }.eachCount()

        fun cheapestTarget(cost: (from: Int, to: Int) -> Int) =
            (min .. max).minOf { to -> withCount.map { (from, count) -> count * cost(from, to) }.sum() }

    }
}
