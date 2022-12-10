package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec

class Day06 : AdventOfCodeSpec({ challenge ->
    challenge.map { it.first() }
        .eval(7, 5, 6, 10, 11, 1100) { line -> line.indexOfMarker(4) + 4 }
        .eval(19, 23, 23, 29, 26, 2421) { line -> line.indexOfMarker(14) + 14 }
})

private fun String.indexOfMarker(size: Int) = asSequence()
    .windowed(size)
    .indexOfFirst { it.toSet().size == size }
