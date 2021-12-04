package com.steno.adventofcode.y2020

import com.steno.assignment

data class Seat(val row: Int, val column: Int) {
    val id
        get() = row * 8 + column
}

private fun main() {
    assignment("2020/day5") { it.map(::parseSeat) }
        .eval { it.map(Seat::id).maxOrNull()!! }
        .eval {
            it.map(Seat::id).sorted()
                .zipWithNext()
                .find { (a, b) -> a + 2 == b }
                ?.first?.let { id -> id + 1 }
        }
}

fun parseSeat(line: String) = Seat(
    line.takeWhile { it in "FB" }
        .replace('F', '0')
        .replace('B', '1')
        .toInt(2),
    line.takeLastWhile { it in "LR" }
        .replace('L', '0')
        .replace('R', '1')
        .toInt(2)
)
