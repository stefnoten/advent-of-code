package com.steno.adventofcode.y2020

import com.steno.assignment

fun main() {
    assignment("2020/day1") { lines -> lines.map { it.toInt() }.toSet() }
        .eval { input ->
            input
                .let { it.find { i -> 2020 - i in it } }!!
                .let { it * (2020 - it) }
        }
        .eval { input ->
            input.firstNotNullOf { i ->
                input.find { j -> 2020 - i - j in input }
                    ?.let { j -> i * j * (2020 - i - j) }
            }
        }
}
