package com.steno.adventofcode.y2020

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach

private class Day05 : AdventOfCodeSpec({ challenge ->
    challenge.mapEach { parseSeat(it) }
        .eval(820, 818) { it.map(Seat::id).maxOrNull()!! }
        .eval(null, 559) {
            it.map(Seat::id).sorted()
                .zipWithNext()
                .find { (a, b) -> a + 2 == b }
                ?.first?.let { id -> id + 1 }
        }
}) {
    data class Seat(val row: Int, val column: Int) {
        val id
            get() = row * 8 + column
    }

    companion object {
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
    }
}
