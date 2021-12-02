package com.steno.adventofcode2021

private val INPUT = resourceFile("/day1/input.txt")

fun main() {
    println("Part 1: ${INPUT.useLinesAs(String::toInt) { timesIncreasing(it) }}")
    println("Part 2: ${INPUT.useLinesAs(String::toInt) { timesIncreasingSlidingWindow(it, 3) }}")
}

fun timesIncreasing(numbers: Sequence<Int>) = numbers.zipWithNext().filter { (a, b) -> b > a }.count()

fun timesIncreasingSlidingWindow(numbers: Sequence<Int>, window: Int) = timesIncreasing(numbers.windowed(window) { it.sum() })
