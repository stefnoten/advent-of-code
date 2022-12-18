package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec

private class Day06: AdventOfCodeSpec({ challenge ->
    challenge.map { lines -> lines.first().split(',').map { Lanternfish(it.toInt()) } }
        .eval(5934, 360268) { initial ->
            generateSequence(initial) { it.nextDay() }
                .elementAt(80)
                .sumOf { it.count }
        }
        .eval(26984457539, 1632146183902) { initial ->
            generateSequence(initial) { it.nextDay() }
                .elementAt(256)
                .sumOf { it.count }
        }
}) {
    companion object {
        data class Lanternfish(val daysUntilCreate: Int, val count: Long = 1) {
            val offspring = when (daysUntilCreate) {
                0 -> Lanternfish(8, count)
                else -> null
            }
            val nextDay
                get() = when (daysUntilCreate) {
                    0 -> Lanternfish(6, count)
                    else -> Lanternfish(daysUntilCreate - 1, count)
                }

            operator fun plus(other: Lanternfish) = when {
                other.daysUntilCreate == daysUntilCreate -> Lanternfish(daysUntilCreate, count + other.count)
                else -> throw IllegalStateException()
            }
        }

        fun List<Lanternfish>.nextDay() = (map { it.nextDay } + mapNotNull { it.offspring }).compact()
        fun List<Lanternfish>.compact() = groupingBy { it.daysUntilCreate }
            .reduce { _, acc, next -> acc + next }
            .values
            .toList()

    }
}

