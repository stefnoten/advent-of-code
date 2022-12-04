package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec

class Day3 : AdventOfCodeSpec({
    parsePerLine { Rucksack.parse(it) }
        .eval { all -> all.sumOf { it.common.sum() } }
        .eval { all ->
            all
                .windowed(3, step = 3) { (a, b, c) -> a.all intersect b.all intersect c.all }
                .sumOf { it.sum() }
        }
}) {
    data class Rucksack(val compartment1: Set<Int>, val compartment2: Set<Int>) {

        val all = compartment1 + compartment2
        val common = compartment1 intersect compartment2

        companion object {
            fun parse(line: String) = Rucksack(
                line.take(line.length / 2).toPriorities(),
                line.takeLast(line.length / 2).toPriorities()
            )

            private fun String.toPriorities() = map {
                when (it.isUpperCase()) {
                    false -> it - 'a' + 1
                    true -> it - 'A' + 27
                }
            }.toSet()
        }
    }
}
