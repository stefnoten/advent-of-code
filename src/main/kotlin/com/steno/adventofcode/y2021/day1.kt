package com.steno.adventofcode.y2021

import com.steno.Assignment

fun main() {
    Assignment("2021/day1", String::toInt)
        .eval { timesIncreasing(it) }
        .eval { timesIncreasingSlidingWindow(it, 3) }
}

fun timesIncreasing(numbers: Sequence<Int>) = numbers.zipWithNext().filter { (a, b) -> b > a }.count()

fun timesIncreasingSlidingWindow(numbers: Sequence<Int>, window: Int) = timesIncreasing(numbers.windowed(window) { it.sum() })
