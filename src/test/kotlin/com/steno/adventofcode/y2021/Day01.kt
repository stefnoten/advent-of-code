package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach

private class Day01: AdventOfCodeSpec({ challenge ->
    challenge.mapEach { it.toInt() }
        .eval(7, 1266) { timesIncreasing(it) }
        .eval(5, 1217) { timesIncreasingSlidingWindow(it, 3) }
}) {
    companion object {
        fun timesIncreasing(numbers: Sequence<Int>) = numbers.zipWithNext().filter { (a, b) -> b > a }.count()

        fun timesIncreasingSlidingWindow(numbers: Sequence<Int>, window: Int) = timesIncreasing(numbers.windowed(window) { it.sum() })

    }
}
